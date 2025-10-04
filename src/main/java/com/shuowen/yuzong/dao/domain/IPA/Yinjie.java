package com.shuowen.yuzong.dao.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.dao.model.PinyinIPA.IPASyllableEntry;
import lombok.Getter;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;


/**
 * 音节DTO类
 * */
public class Yinjie
{
    protected String pinyin;
    @Getter
    protected Map<String, String> info;
    protected String code;
    @Getter
    protected boolean valid = false;
    protected static String INVAILD_DICT = "不正确的字典，拼音：";

    public Yinjie(IPASyllableEntry ipa)
    {
        if (ipa == null) return;

        pinyin = ipa.getStandard();

        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
        Map<String, String> tmp = new HashMap<>();
        info.forEach((k, v) -> tmp.put(k.toLowerCase(), v));
        info = tmp;

        code = ipa.getCode();

        valid = true;
    }

    public static Yinjie of(IPASyllableEntry ipa)
    {
        return new Yinjie(ipa);
    }

    public String getInfo(String dict)
    {
        return info.getOrDefault(dict.toLowerCase(), INVAILD_DICT + pinyin);
    }

    public static List<Yinjie> listOf(List<IPASyllableEntry> ipa)
    {
        List<Yinjie> l = new ArrayList<>();
        for (var i : ipa) l.add(of(i));
        return l;
    }

    public static Map<String, Yinjie> MapOf(List<IPASyllableEntry> ipa)
    {
        Map<String, Yinjie> m = new HashMap<>();
        for (var i : ipa) m.put(i.getStandard(), of(i));
        return m;
    }

}
