package com.shuowen.yuzong.service.impl.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.data.domain.Reference.Keyword;
import com.shuowen.yuzong.data.domain.Reference.RefPage;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.mapper.Refer.DictMapper;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.data.model.Reference.RefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

@Service
public class RefService
{
    @Autowired
    private RefMapper ck;

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

    /**
     * 初始化词典
     */
    public void initDictionary(String... dictionary)
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

    /**
     * 通过字典代号和排序这两个联合唯一键寻找一页数据
     */
    public RefPage getPageInfo(String dictionary, String sort)
    {
        return new RefPage(ck.getPageBySort(dictionary, sort));
    }

    /**
     * 模糊查询，查询到那一页的开头
     */
    public List<SearchResult> findContent(String dictionary, String query)
    {
        List<SearchResult> ans = new ArrayList<>();
        for (var i : ck.getItemsByQuery(dictionary, query))
        {
            var tmp = new SearchResult();
            tmp.setTitle(i.getContent());
            tmp.setExplain("");  //不需要这个字段
            tmp.setTag(i.getPage().toString());
            tmp.setInfo(Map.of("dict", i.getDictionary(), "sort",
                    getPageInfo(dictionary, i.getSort()).encode().getFrontSort()));
            ans.add(tmp);
        }
        return ans;
    }

    /**
     * @param before 插入页在当前页面的位置 {@code true} - 前面，sort是开头的序号  {@code false} - 后面，sort是结尾的序号
     */
    @Transactional (rollbackFor = {Exception.class})
    public RefPage insertPage(String dictionary, String sort, boolean before)
    {
        var find = ck.findNearby(dictionary, sort, before);
        String left, right;
        if (before)
        {   //  第1页结束标记（找到的） <插入位置>  第2页开始标记
            left = find.getSort(); right = sort;
        }
        else
        {   //  第2页结束标记 <插入位置>  第3页开始标记（找到的）
            left = sort; right = find.getSort();
        }
        var list = RefEntity.initPage(dictionary, FractionIndex.of(left), FractionIndex.of(right));

        ck.insert(list);
        return new RefPage(list).encode();
    }

    @Transactional (rollbackFor = {Exception.class})
    public void updatePage(String dictionary, RefPage page)
    {
        page = page.decode();
        ck.delete(dictionary, page.getFrontSort(), page.getEndSort());

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
    public Pair<Maybe<String>, Maybe<String>> getNearBy(String dictioary, Pair<String, String> sorts)
    {
        var left = Maybe.exist(ck.findNearby(dictioary, sorts.getLeft(), true));
        var right = Maybe.exist(ck.findNearby(dictioary, sorts.getRight(), false));

        if (Keyword.isBookEdge(left.getValue().getContent())) left = Maybe.nothing();
        if (Keyword.isBookEdge(right.getValue().getContent())) right = Maybe.nothing();

        Function<RefEntity, String> fun = i -> getPageInfo(dictioary, i.getSort()).encode().getFrontSort();

        return Pair.of(left.handleIfExist(fun), right.handleIfExist(fun));
    }
}
