package com.shuowen.yuzong.Tool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.dataStructure.UChar;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.service.impl.KeyValueService;
import lombok.Data;

import java.util.*;

/**
 * 正字法规则
 */
@Data
public class OrthoCharset
{
    Map<UChar, UChar> handle = new HashMap<>();

    public OrthoCharset()
    {
    }

    public OrthoCharset(Dialect d)
    {
        var map = JsonTool.readJson(KeyValueService.get("ortho-charset:" + d),
                new TypeReference<Map<String, String>>() {}, new ObjectMapper());

        for (var i : map.entrySet())
            handle.put(UChar.of(i.getKey()), UChar.of(i.getValue()));
    }

    public void addIgnore(UChar ignore)
    {
        handle.put(ignore, UChar.of("-"));
    }

    public void addIgnores(Collection<UChar> ignores)
    {
        for (var i : ignores) addIgnore(i);
    }

    public void addRule(UChar tc, UChar sc)
    {
        handle.put(tc, sc);
    }

    public void addRules(Map<UChar, UChar> rule)
    {
        for (var i : rule.entrySet()) addRule(i.getKey(), i.getValue());
    }

    public UChar choose(UChar original, UChar translation)
    {
        // 通过原字符寻找一下是否特殊，是就使用规则表里的，否则还是用原字符
        // 如果规则表里没有写，返回
        if (!handle.containsKey(original)) return translation;
        else
        {
            var value = handle.get(original);
            if (value.contentEquals("-")) return original;
            else return value;
        }
    }
}
