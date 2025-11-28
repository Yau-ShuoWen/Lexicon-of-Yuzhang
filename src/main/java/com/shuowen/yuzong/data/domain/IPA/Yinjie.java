package com.shuowen.yuzong.data.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.data.model.IPA.IPASyllableEntity;
import lombok.Data;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;
import static com.shuowen.yuzong.Tool.format.JsonTool.toJson;


/**
 * 音节DTO类
 */
@Data
public class Yinjie
{
    protected String pinyin;
    protected Map<String, String> info;
    protected String code;
    protected boolean valid = false;
    protected static String INVAILD_DICT = "不正确的字典，拼音：";

    public Yinjie(IPASyllableEntity ipa)
    {
        if (ipa == null) return;

        pinyin = ipa.getStandard();
        code = ipa.getCode();

        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
        Map<String, String> tmp = new HashMap<>();
        info.forEach((k, v) -> tmp.put(k.toLowerCase(), v));
        info = tmp;

        valid = true;
    }

    public static Yinjie of(IPASyllableEntity ipa)
    {
        return new Yinjie(ipa);
    }

    public Yinjie(YinjiePart i1, YinjiePart i2)
    {
        pinyin = i1.pinyin + i2.pinyin;
        code = (i1.code + i2.code).replace("~", "");
        info = new HashMap<>();
        for (var i : i1.getInfo().keySet())
        {
            String ipa = i1.getInfo().get(i) + i2.getInfo().get(i);
            if (ipa.contains("-")) ipa = "-";
            info.put(i.toLowerCase(), ipa);
        }

        valid = true;
    }

    public static Yinjie of(YinjiePart i1, YinjiePart i2)
    {
        return new Yinjie(i1, i2);
    }

    public String getInfo(String dict)
    {
        return info.getOrDefault(dict.toLowerCase(), INVAILD_DICT + pinyin);
    }

    public IPASyllableEntity transfer()
    {
        IPASyllableEntity ans = new IPASyllableEntity();
        ans.setStandard(pinyin);
        ans.setCode(code);
        ans.setInfo(toJson(info, new ObjectMapper()));
        return ans;
    }
}
