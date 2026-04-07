package com.shuowen.yuzong.service.impl.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.DictGroup;
import com.shuowen.yuzong.data.mapper.Reference.DictMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional (rollbackFor = {Exception.class})
public class DictService
{
    @Autowired
    private DictMapper cd;

    /**
     * 获得由「词典代号」查询「名称」的查询表
     */
    public DictGroup getDictionaryMap(Dialect d, Language l)
    {
        Map<DictCode, String> ans = new HashMap<>();
        for (var i : cd.findByDialect(d.toString()))
        {
            ScTcText name = JsonTool.readJson(i.getName(), new TypeReference<>() {}, new ObjectMapper());
            ans.put(new DictCode(i.getCode()), name.get(l).toString());
        }
        return new DictGroup(ans);
    }

    /**
     * 把「词典代号」查询「名称」按照序列的方法排列，用于前端
     */
    public List<Pair<String, DictCode>> getDictionaryMenu(Dialect dialect)
    {
        List<Pair<String, DictCode>> ans = new ArrayList<>();
        for (var i : getDictionaryMap(dialect, Language.TC).getDict().entrySet())
            ans.add(Pair.of(i.getValue(), i.getKey()));
        return ans;
    }

    private static DictService instance;

    @PostConstruct
    public void init()
    {
        instance = this;
    }

    public static DictGroup getDictionary(Dialect d, Language l)
    {
        return instance.getDictionaryMap(d, l);
    }

}
