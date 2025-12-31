package com.shuowen.yuzong.data.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.data.model.IPA.IPASyllableEntity;
import lombok.Getter;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

/**
 * 用于组合的音节DTO类
 */
@Getter
public class Shengyun
{
    protected String pinyin;
    protected Map<String, String> info;
    protected String code;

    public Shengyun(IPASyllableEntity ipa)
    {
        if (ipa == null) return;

        pinyin = ipa.getStandard();
        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
        code = ipa.getCode();
    }

    public static Shengyun of(IPASyllableEntity ipa)
    {
        return new Shengyun(ipa);
    }
}
