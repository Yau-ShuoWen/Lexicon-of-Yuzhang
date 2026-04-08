package com.shuowen.yuzong.service.impl.Reference;

import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.ObfString;
import com.shuowen.yuzong.data.domain.Reference.*;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Service
@Transactional (rollbackFor = {Exception.class})
public class RefService
{
    @Autowired
    private RefMapper ck;

    @Autowired
    private RefProofService pr;

    @Autowired
    private RefDraftService dr;

    /**
     * 获取一页的所有信息
     */
    private List<RefEntity> getPageInfo(DictCode dict, FractionIndex sort)
    {
        return ck.getPageBySort(dict.getCode(), sort.toString());
    }

    /**
     * 通过字典代号，和排序，获得一整页（不确定的格式）
     */
    public Page getRefPage(DictCode dict, FractionIndex sort)
    {
        var list = getPageInfo(dict, sort);
        if (list.isEmpty()) throw new RuntimeException();//

        // 如果字段被锁定了，返回校对类，否则返回草稿类
        return list.get(0).getLocked() ?
                RefProof.of(list) : RefDraft.of(list);
    }

    /**
     * 获得书的页码目录
     */
    public List<Pair<FractionIndex, String>> getCatalog(DictCode dict)
    {
        return ListTool.mapping(ck.findPageinfo(dict.getCode()), i -> Pair.of(
                i.getTheSort(),
                String.format("%s 第 %s 頁", i.getThePageInfo().getLeft(), i.getThePageInfo().getRight()))
        );
    }

    /**
     * 模糊查询，查询到那一页的开头序号
     */
    public List<SearchResult> findContent(DictCode dict, String query)
    {
        if (!StringTool.isTrimValid(query)) return List.of();
        if (query.equals("{") || query.equals("}")) return List.of();
        if (query.equals("[") || query.equals("]")) return List.of();

        List<SearchResult> ans = new ArrayList<>();
        for (var i : ck.getItemsByQuery(dict.getCode(), query))
        {
            if (Keyword.isBookEdge(i.getContent())) continue; // 不能找开头结尾关键词，创建页面会失败
            if (Keyword.isPageEdge(i.getContent())) continue; // 不能找页面开头结尾关键词，因为每一页都有，没有参考价值

            var tmp = new SearchResult();

            // 以 {} 開頭，後面接 []，中間可有空白，最後還要有一個空白」的字串
            var a = RichTextUtil.buildSnippet(i.getContent(), query, 20,
                    Pattern.compile("^\\{[^}]*}\\s*\\[[^]]*]\\s"));
            tmp.setTitle(a.getLeft());
            tmp.setExplain(a.getRight());
            tmp.setTag(i.getThePageInfo().getRight().toString());

            var sort = Page.readList(getPageInfo(dict, i.getTheSort()))
                    .get(0).getSort();

            tmp.setInfo(Map.of("dict", i.getDictionary(), "sort", ObfString.encode(sort.toString())));

            ans.add(tmp);
        }
        return ans;
    }

    /**
     * 返回无效的情况有：
     * <br>1. 没找到，设计出来不可能，所以直接使用exist，如果出现说明设计缺陷
     * <br>2. 旁边的就是书的边界了，这时候就是正常状态的无结果了
     */
    public Twin<Maybe<FractionIndex>> getNearBy(DictCode dict, Twin<FractionIndex> sorts)
    {
        var nearby = Twin.of(
                Maybe.exist(ck.findNearby(dict.getCode(), sorts.getLeft().toString(), true)),
                Maybe.exist(ck.findNearby(dict.getCode(), sorts.getRight().toString(), false))
        );
        // 如果前后的内容是数的边界的标志，说明没有这一页，换成nothing
        nearby = nearby.map(i -> Keyword.isBookEdge(i.getValue().getContent()) ? Maybe.nothing() : i);

        // 如果有这一页，找到这一页的开头的标号，左右侧独立计算
        return nearby.map(i -> i.handleIfExist(
                j -> getPageInfo(dict, j.getTheSort()).get(0).getTheSort()
        ));
    }

    /**
     * 插入页面，获取草稿类
     */
    public RefDraft insertPage(DictCode dict, FractionIndex sort, boolean before)
    {
        return dr.insertPage(dict, sort, before);
    }

    /**
     * 提交草稿类，覆写页面
     */
    public void overwritePage(DictCode dict, RefDraft page)
    {
        dr.overwritePage(dict, page);
    }

    /**
     * 锁定页面，并返回新的数据格式
     */
    public RefProof lockPage(DictCode dict, FractionIndex sort)
    {
        var list = getPageInfo(dict, sort);

        // 提取出所有id，在数据库层设定
        ck.lockPage(dict.getCode(), ListTool.mapping(list, RefEntity::getId));

        // 返回新的数据格式
        return RefProof.of(getPageInfo(dict, sort));
    }

    /**
     * 编辑界面
     */
    public void editPage(DictCode dict, RefProof page)
    {
        pr.editPage(dict, page);
    }

    /**
     * 删除界面，返回周围界面（不确定的格式）
     */
    public Page deletePage(DictCode dict, FractionIndex frontSort)
    {
        // 通过开始的顺序获得整页的数据
        var page = RefDraft.of(getPageInfo(dict, frontSort));

        // 在删除之前寻找上下文，如果都没有上下文
        var nearby = getNearBy(dict, Twin.of(page.getFrontSort(), page.getEndSort()));
        if (nearby.both(Maybe::isEmpty)) throw new RuntimeException("只剩下一页的页面不可以删除");

        // 根据开头结尾的序号清空中间，之后清除开头结尾
        String dictionary = dict.getCode();
        String front = page.getFrontSort().toString();
        String end = page.getEndSort().toString();

        ck.deleteInside(dictionary, front, end);
        ck.deleteEdge(dictionary, front);
        ck.deleteEdge(dictionary, end);

        // 如果有下一页，跳转下一页，否则上一页
        var newPageSort = (nearby.getRight().isValid() ? nearby.getRight() : nearby.getLeft()).getValue();
        return getRefPage(dict, newPageSort);
    }

    /**
     * 重建索引
     */
    public void rebuildSort(DictCode dict)
    {
        var ids = ck.getAllItemId(dict.getCode());
        var sorts = FractionIndex.rebuild(ids.size());
        List<Pair<Integer, String>> update = new ArrayList<>();
        for (int i = 0; i < sorts.size(); i++)
        {
            update.add(Pair.of(ids.get(i), "#" + sorts.get(i).toString()));
        }
        ck.updateAllSort(dict.getCode(), update);
        ck.recoverSort(dict.getCode());
    }
}
