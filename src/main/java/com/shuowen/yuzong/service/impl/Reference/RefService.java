package com.shuowen.yuzong.service.impl.Reference;

import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.ObfString;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.Keyword;
import com.shuowen.yuzong.data.domain.Reference.RefPage;
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

    /**
     * 初始化词典
     */
    public void initBook(String... dictionary)
    {
        for (String d : dictionary)
        {
            if (ck.isDictionaryExist(d))
            {
                System.out.println("词典" + d + "已经初始化了");
            }
            else
            {
                ck.insert(RefEntity.initBook(d));
                System.out.println("词典" + d + "加入成功");
            }
        }
    }

    public List<RefEntity> getPage(String dictionary, String sort)
    {
        return ck.getPageBySort(dictionary, sort);
    }

    /**
     * 模糊查询，查询到那一页的开头序号
     */
    public List<SearchResult> findContent(String dictionary, String query)
    {
        if(!StringTool.isTrimValid(query)) return List.of();
        if(query.equals("{")||query.equals("}")) return List.of();
        if(query.equals("[")||query.equals("]")) return List.of();

        List<SearchResult> ans = new ArrayList<>();
        for (var i : ck.getItemsByQuery(dictionary, query))
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

            var sort = RefPage.of(getPage(dictionary, i.getSort())).getFrontSort();
            tmp.setInfo(Map.of("dict", i.getDictionary(), "sort", ObfString.encode(sort.toString())));

            ans.add(tmp);
        }
        return ans;
    }

    public RefPage getPageSpecial(String dict, String query)
    {
        if (!ck.isDictionaryNotEmpty(dict)) throw new NoSuchElementException("字典数据为空");

        return switch (query)
        {
            case "first-page" ->
            {
                var item = ck.getItemsByQuery(dict, Keyword.FRONT_OF_BOOK);
                var sort = ListTool.checkSizeOne(item, "", "").getSort();
                var pageEdge = ck.findNearby(dict, sort, false);
                if (!Keyword.FRONT_OF_PAGE.equals(pageEdge.getContent()))
                    throw new RuntimeException("对不上");
                yield RefPage.of(getPage(dict, pageEdge.getSort()));
            }

            case "last-page" ->
            {
                var item = ck.getItemsByQuery(dict, Keyword.END_OF_BOOK);
                var sort = ListTool.checkSizeOne(item, "", "").getSort();
                var pageEdge = ck.findNearby(dict, sort, true);
                if (!Keyword.END_OF_PAGE.equals(pageEdge.getContent()))
                    throw new RuntimeException("对不上");
                yield RefPage.of(getPage(dict, pageEdge.getSort()));
            }

            case "random" ->
            {
                var item = ck.getItemByRandom(dict);
                yield RefPage.of(getPage(dict, item.getSort()));
            }

            default -> throw new NoSuchElementException("关键词错误");
        };
    }

    /**
     * @param before 插入页在当前页面的位置
     *               <br>{@code true} - 前面，sort是开头的序号  {@code false} - 后面，sort是结尾的序号
     */
    @Transactional (rollbackFor = {Exception.class})
    public RefPage insertPage(String dictionary, FractionIndex sort, boolean before)
    {
        var find = ck.findNearby(dictionary, sort.toString(), before);

        // before == true  第1页结束标记（找到的） | <插入位置> | 第2页开始标记（发来的）
        // before == false 第2页结束标记（发来的） | <插入位置> | 第3页开始标记（找到的）
        // 所以如果false 就要翻转找到的和发来的位置
        var sorts = Twin.of(FractionIndex.of(find.getSort()), sort);
        if (!before) sorts.swap();

        var list = RefEntity.initPage(dictionary, sorts);
        ck.insert(list);
        return RefPage.of(list);
    }

    @Transactional (rollbackFor = {Exception.class})
    public void updatePage(String dictionary, RefPage page)
    {
        ck.deleteInside(dictionary, page.getFrontSort().toString(), page.getEndSort().toString());

        var entity = page.transfer();
        ck.updateEdge(entity.getLeft().getLeft());
        ck.updateEdge(entity.getLeft().getRight());
        ck.insert(entity.getRight());
    }

    /**
     * 返回无效的情况有：
     * <br>1. 没找到，设计出来不可能，所以直接使用exist，如果出现说明设计缺陷
     * <br>2. 旁边的就是书的边界了，这时候就是正常状态的无结果了
     */
    public Twin<Maybe<FractionIndex>> getNearBy(String dictioary, Twin<FractionIndex> sorts)
    {
        var nearby = Twin.of(
                Maybe.exist(ck.findNearby(dictioary, sorts.getLeft().toString(), true)),
                Maybe.exist(ck.findNearby(dictioary, sorts.getRight().toString(), false))
        );
        // 如果前后的内容是数的边界的标志，说明没有这一页，换成nothing
        nearby = nearby.map(i -> Keyword.isBookEdge(i.getValue().getContent()) ? Maybe.nothing() : i);

        // 如果有这一页，找到这一页的开头的标号，左右侧独立计算
        return nearby.map(i -> i.handleIfExist(
                j -> RefPage.of(getPage(dictioary, j.getSort())).getFrontSort()
        ));
    }

    /**
     * 删除界面，返回周围的一个页面
     */
    public RefPage deletePage(String dictionary, FractionIndex frontSort)
    {
        // 通过开始的顺序获得整页的数据
        var page = RefPage.of(getPage(dictionary, frontSort.toString()));

        // 在删除之前寻找上下文，如果都没有上下文
        var nearby = getNearBy(dictionary, Twin.of(page.getFrontSort(), page.getEndSort()));
        if (nearby.both(Maybe::isEmpty)) throw new RuntimeException("只剩下一页的页面不可以删除");

        // 根据开头结尾的序号清空中间，之后清除开头结尾
        String front = page.getFrontSort().toString();
        String end = page.getEndSort().toString();
        ck.deleteInside(dictionary, front, end);
        ck.deleteEdge(dictionary, front);
        ck.deleteEdge(dictionary, end);

        // 如果有下一页，跳转下一页，否则上一页
        var newPageSort = (nearby.getRight().isValid() ? nearby.getRight() : nearby.getLeft()).getValue().toString();
        return RefPage.of(getPage(dictionary, newPageSort));
    }

    /**
     * 获得书的页码目录
     */
    public List<Pair<FractionIndex, String>> getCatalog(String dictionary)
    {
        return ListTool.mapping(ck.findPageinfo(dictionary), i -> Pair.of(
                i.getTheSort(),
                String.format("%s 第%s頁", i.getThePageInfo().getLeft(), i.getThePageInfo().getRight()))
        );
    }

    @Transactional (rollbackFor = {Exception.class})
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
