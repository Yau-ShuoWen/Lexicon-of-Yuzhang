package com.shuowen.yuzong.data.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
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
    protected Map<String, String> info;

    public Shengdiao(IPAToneEntity ipa)
    {
        tone = ipa.getStandard();
        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
    }

    public static Maybe<Shengdiao> tryOf(IPAToneEntity ipa)
    {
        if (ipa == null) return Maybe.nothing();
        else return Maybe.exist(new Shengdiao(ipa));
    }

    public Maybe<String> getInfo(String dict)
    {
        return Maybe.uncertain(info.get(dict));
    }
}
