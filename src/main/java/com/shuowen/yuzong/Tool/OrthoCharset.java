package com.shuowen.yuzong.Tool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.TextTool.Punctuation;
import com.shuowen.yuzong.Tool.dataStructure.UChar;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.service.impl.KeyValueService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 正字法规则
 */
public class OrthoCharset
{
    /**
     * 全局缓存，一个map，每一个方言都有对应的规则
     */
    private static final Map<Dialect, OrthoCharset> CACHE = new ConcurrentHashMap<>();

    /**
     * 没有方言的对应规则
     */
    private static final OrthoCharset DEFAULT = new OrthoCharset();

    public static OrthoCharset of()
    {
        return DEFAULT;
    }

    public static OrthoCharset of(Dialect d)
    {
        return CACHE.computeIfAbsent(d, OrthoCharset::new);
    }

    Map<UChar, UChar> handle = new HashMap<>();

    private OrthoCharset()
    {
        var map = JsonTool.readJson(KeyValueService.get("ortho-charset"),
                new TypeReference<Map<UChar, UChar>>() {});

        handle.putAll(map);

        addIgnores(Punctuation.getCharset()); // 在方言的基础上加上标点符号的规则
    }

    private OrthoCharset(Dialect d)
    {
        handle.putAll(JsonTool.readJson(KeyValueService.get("ortho-charset"), new TypeReference<Map<UChar, UChar>>() {}));
        handle.putAll(JsonTool.readJson(KeyValueService.get("ortho-charset:" + d), new TypeReference<Map<UChar, UChar>>() {}));

        addIgnores(Punctuation.getCharset()); // 在方言的基础上加上标点符号的规则
    }

    /**
     * 不特殊处理的字符
     */
    public void addIgnore(UChar ignore)
    {
        handle.put(ignore, UChar.of("-"));
    }

    /**
     * 不特殊处理的字符集合
     */
    public void addIgnores(Collection<UChar> ignores)
    {
        for (var i : ignores) addIgnore(i);
    }

    /**
     * 特殊转换的字符集合
     */
    public void addRule(UChar tc, UChar sc)
    {
        handle.put(tc, sc);
    }

    /**
     * 特殊转换的字符集合
     */
    public void addRules(Map<UChar, UChar> rule)
    {
        handle.putAll(rule);
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
