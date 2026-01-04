package com.shuowen.yuzong.data.domain.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.data.model.IPA.IPASyllableEntity;
import lombok.Getter;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

/**
 * 音段领域模型类
 */
@Getter
public class Shengyun
{
    protected String pinyin;
    protected Map<String, String> info;
    protected String code;

    private Shengyun(IPASyllableEntity ipa)
    {
        pinyin = ipa.getStandard();
        info = readJson(ipa.getInfo(), new TypeReference<>() {}, new ObjectMapper());
        code = ipa.getCode();
    }

    /**
     * 使用一个不确定是否有效的IPASyllableEntity初始化
     */
    public static Maybe<Shengyun> tryOf(IPASyllableEntity ipa)
    {
        if (ipa == null) return Maybe.nothing();
        else return Maybe.exist(new Shengyun(ipa));
    }

    /**
     * 获得查询表
     */
    public static Map<String, Shengyun> mapOf(List<IPASyllableEntity> list)
    {
        Map<String, Shengyun> map = new HashMap<>();
        for (var i : list) map.put(i.getCode(), new Shengyun(i));
        return map;
    }
}
