package com.shuowen.yuzong.data.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.data.model.IPA.IPAToneEntity;
import lombok.Getter;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

/**
 * 音调DTO类
 * */
public class Shengdiao
{
    protected Integer tone;
    @Getter
    protected Map<String, String> info;
    @Getter
    protected boolean valid = false;
    protected static String INVAILD = "不正确的字典，声调：";

    public Shengdiao(IPAToneEntity ipa)
    {
        if (ipa == null) return;

        tone = ipa.getStandard();

        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
        Map<String, String> tmp = new HashMap<>();
        info.forEach((k, v) -> tmp.put(k.toLowerCase(), v));
        info = tmp;

        valid = true;
    }

    public static Shengdiao of(IPAToneEntity ipa)
    {
        return new Shengdiao(ipa);
    }

    public String getInfo(String dict)
    {
        return info.getOrDefault(dict.toLowerCase(), INVAILD + tone);
    }

    public static List<Shengdiao> listOf(List<IPAToneEntity> ipa)
    {
        List<Shengdiao> l = new ArrayList<>();
        for (var i : ipa) l.add(of(i));
        return l;
    }

    public static Map<Integer, Shengdiao> MapOf(List<IPAToneEntity> ipa)
    {
        Map<Integer, Shengdiao> m = new HashMap<>();
        for (var i : ipa) m.put(i.getStandard(), of(i));
        return m;
    }
}
