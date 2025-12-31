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
        valid = true;
    }

    public static Yinjie of(IPASyllableEntity ipa)
    {
        return new Yinjie(ipa);
    }

    /**
     * 从声母和韵母拼接而成
     */
    public Yinjie(Shengyun initial, Shengyun last)
    {
        pinyin = initial.pinyin + last.pinyin;
        code = (initial.code + last.code).replace("~", "");
        info = new HashMap<>();
        for (var i : initial.getInfo().keySet())
        {
            String ipa = initial.getInfo().get(i) + last.getInfo().get(i);
            if (ipa.contains("-")) ipa = "-";
            info.put(i, ipa);
        }

        valid = true;
    }

    public static Yinjie of(Shengyun i1, Shengyun i2)
    {
        return new Yinjie(i1, i2);
    }

    public String getInfo(String dict)
    {
        return info.getOrDefault(dict, INVAILD_DICT + pinyin);
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
