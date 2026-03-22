package com.shuowen.yuzong.data.domain.Reference;

import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import lombok.Getter;

import java.util.*;

@Getter
public class DictGroup
{
    Map<DictCode, String> dict;

    public DictGroup(Map<DictCode, String> dict)
    {
        this.dict = dict;
    }

    public String getName(DictCode di, Language l)
    {
        String name = dict.get(new DictCode(di.getCode()));

        name = String.format("《%s》", name);
        if (di.isStrict()) name += ScTcText.get("（嚴式標音）", l);

        return name;
    }

    public boolean containDict(DictCode di)
    {
        return dict.containsKey(new DictCode(di.getCode()));
    }

    public Set<DictCode> getKeySet()
    {
        Set<DictCode> keys = new HashSet<>();
        for (var i : dict.keySet())
        {
            keys.add(new DictCode(i.getCode(), true));
            keys.add(new DictCode(i.getCode(), false));
        }
        return keys;
    }
}
