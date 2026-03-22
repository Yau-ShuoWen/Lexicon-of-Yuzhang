package com.shuowen.yuzong.data.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.model.IPA.IPAToneEntity;
import lombok.Getter;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

/**
 * 音调领域模型类
 */
public class Shengdiao
{
    protected Integer tone;
    @Getter
    protected Map<DictCode, String> info;

    public Shengdiao(IPAToneEntity ipa)
    {
        tone = ipa.getStandard();
        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
    }

    public static Map<Integer, Shengdiao> mapOf(Set<IPAToneEntity> set)
    {
        Map<Integer, Shengdiao> map = new HashMap<>();
        for (var i : set)
        {
            if (i == null) continue;
            map.put(i.getStandard(), new Shengdiao(i));
        }
        return map;
    }

    public String getInfo(DictCode dict)
    {
        // 如果等于"-"，改成null，这是数据库明示这里没有数据的方式
        return "-".equals(info.get(dict)) ? null : info.get(dict);
    }
}
