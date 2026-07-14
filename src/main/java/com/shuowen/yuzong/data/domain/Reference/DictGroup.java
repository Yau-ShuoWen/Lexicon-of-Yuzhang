package com.shuowen.yuzong.data.domain.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.service.impl.Reference.DictService;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DictGroup
{
    private static final Map<Dialect, DictGroup> CACHE = new ConcurrentHashMap<>();

    public static DictGroup of(Dialect d)
    {
        return CACHE.computeIfAbsent(d, DictGroup::new);
    }

    Map<DictCode, ScTcText> dict = new HashMap<>();

    private DictGroup(Dialect d)
    {
        for (var i : DictService.getDicts(d))
        {
            ScTcText name = JsonTool.readJson(i.getName(), new TypeReference<>() {});
            dict.put(new DictCode(i.getCode()), name);
        }
    }

    public String getName(DictCode di, Language l)
    {
        return String.format("《%s》", dict.get(di).get(l));
    }

    public String getName(DictCodeExt di, Language l)
    {
        return String.format("%s%s",
                getName(di.getCode(), l),
                (di.isStrict() ? ScTcText.get("（嚴式標音）", "（严式标音）", l) : "")
        );
    }

    public boolean containDict(DictCode di)
    {
        return dict.containsKey(new DictCode(di.getCode()));
    }

    public Set<DictCode> getKeySet()
    {
        return dict.keySet();
    }
}
