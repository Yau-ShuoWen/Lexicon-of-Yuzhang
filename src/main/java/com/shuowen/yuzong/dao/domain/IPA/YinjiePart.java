package com.shuowen.yuzong.dao.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.dao.model.IPA.IPASyllableEntity;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

/**
 * 用于组合的音节DTO类
 */
public class YinjiePart
{
    @Getter
    protected String pinyin;
    @Getter
    protected Map<String, String> info;
    @Getter
    protected String code;

    public YinjiePart(IPASyllableEntity ipa)
    {
        if (ipa == null) return;

        pinyin = ipa.getStandard();

        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
        Map<String, String> tmp = new HashMap<>();
        info.forEach((k, v) -> tmp.put(k.toLowerCase(), v));
        info = tmp;

        code = ipa.getCode();
    }

    public static YinjiePart of(IPASyllableEntity ipa)
    {
        return new YinjiePart(ipa);
    }
}

