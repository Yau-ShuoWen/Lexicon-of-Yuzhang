package com.shuowen.yuzong.util.text;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.map.KV;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 正字法规则
 */
public class OrthoCharset
{

    /**
     * 全局缓存，每一个方言都有对应的规则
     */
    private static final Map<Dialect, OrthoCharset> CACHE = new ConcurrentHashMap<>();

    /**
     * 无方言默认规则，懒加载
     */
    private static class DefaultHolder
    {
        private static final OrthoCharset INSTANCE = new OrthoCharset();
    }

    public static OrthoCharset of()
    {
        return DefaultHolder.INSTANCE;
    }

    public static OrthoCharset of(Dialect d)
    {
        return CACHE.computeIfAbsent(d, OrthoCharset::new);
    }

    /**
     * 字符转换规则
     */
    private final Map<UChar, UChar> handle;

    /**
     * 默认规则
     */
    private OrthoCharset()
    {
        Map<UChar, UChar> map = new HashMap<>();

        var rule = JsonTool.readJson(
                KV.get("ortho-charset"),
                new TypeReference<Map<UChar, UChar>>() {}
        );

        if (rule != null)
        {
            map.putAll(rule);
        }

        addIgnores(map, Punctuation.getCharset());

        handle = Collections.unmodifiableMap(map);
    }

    /**
     * 方言规则
     */
    private OrthoCharset(Dialect d)
    {
        Map<UChar, UChar> map = new HashMap<>();

        var common = JsonTool.readJson(
                KV.get("ortho-charset"),
                new TypeReference<Map<UChar, UChar>>() {}
        );

        if (common != null)
        {
            map.putAll(common);
        }

        var dialect = JsonTool.readJson(
                KV.get("ortho-charset:" + d),
                new TypeReference<Map<UChar, UChar>>() {}
        );

        if (dialect != null)
        {
            map.putAll(dialect);
        }

        addIgnores(map, Punctuation.getCharset());

        handle = Collections.unmodifiableMap(map);
    }

    /**
     * 不特殊处理的字符
     */
    private static void addIgnore(Map<UChar, UChar> map, UChar ignore)
    {
        map.put(ignore, UChar.of("-"));
    }

    /**
     * 不特殊处理的字符集合
     */
    private static void addIgnores(Map<UChar, UChar> map, Collection<UChar> ignores)
    {
        for (var i : ignores)
        {
            addIgnore(map, i);
        }
    }

    /**
     * 特殊转换规则
     */
    private static void addRule(Map<UChar, UChar> map, UChar tc, UChar sc)
    {
        map.put(tc, sc);
    }

    /**
     * 特殊转换规则集合
     */
    private static void addRules(Map<UChar, UChar> map, Map<UChar, UChar> rule)
    {
        map.putAll(rule);
    }

    /**
     * 根据规则选择字符
     *
     * @param original    原字符
     * @param translation 已转换字符
     */
    public UChar choose(UChar original, UChar translation)
    {

        if (!handle.containsKey(original))
        {
            return translation;
        }

        var value = handle.get(original);

        if (value.contentEquals("-"))
        {
            return original;
        }

        return value;
    }
}