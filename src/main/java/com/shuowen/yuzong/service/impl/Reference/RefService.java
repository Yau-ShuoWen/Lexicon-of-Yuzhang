package com.shuowen.yuzong.service.impl.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.Obfuscation;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.data.domain.Reference.Keyword;
import com.shuowen.yuzong.data.domain.Reference.RefPage;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.mapper.Reference.DictMapper;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional (rollbackFor = {Exception.class})
public class RefService
{
    @Autowired
    private DictMapper cd;

    /**
     * 获得由「词典代号」查询「名称」的查询表
     */
    public Map<String, String> getDictionaryMap(Dialect d, Language l)
    {
        Map<String, String> ans = new HashMap<>();
        for (var i : cd.findByDialect(d.toString()))
        {
            Map<String, String> tmp = JsonTool.readJson(i.getName(), new TypeReference<>() {}, new ObjectMapper());
            ans.put(i.getCode(), tmp.get(l.toString()));
        }
        return ans;
    }

    /**
     * 把「词典代号」查询「名称」按照序列的方法排列，用于前端
     */
    public List<Pair<String, String>> getDictionaryMenu(Dialect dialect)
    {
        List<Pair<String, String>> ans = new ArrayList<>();
        for (var i : getDictionaryMap(dialect, Language.TC).entrySet())
            ans.add(Pair.of(i.getValue(), i.getKey()));
        return ans;
    }


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
        List<SearchResult> ans = new ArrayList<>();
        for (var i : ck.getItemsByQuery(dictionary, query))
        {
            if (Keyword.isBookEdge(i.getContent())) continue; // 不能找开头结尾关键词，创建页面会失败
            if (Keyword.isPageEdge(i.getContent())) continue; // 不能找页面开头结尾关键词，因为每一页都有，没有参考价值

            var tmp = new SearchResult();
            tmp.setTitle(StringTool.limitLength(i.getContent(), 30, "  ……"));
            tmp.setExplain("");  // 暂时不需要这个字段
            tmp.setTag(i.getPage().toString());

            var sort = RefPage.of(getPage(dictionary, i.getSort())).getFrontSort();
            tmp.setInfo(Map.of("dict", i.getDictionary(), "sort",
                    Obfuscation.encode(sort.toString()))
            );

            ans.add(tmp);
        }
        return ans;
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
        ck.updateEdge(entity.getLeft().get(0));
        ck.updateEdge(entity.getLeft().get(1));
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
}
