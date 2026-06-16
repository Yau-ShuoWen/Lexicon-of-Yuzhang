package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Linguistics.IPA.IPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Quadruple;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinConfig;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.service.impl.IPA.IPAService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IPACache
{
    private static final Map<Dialect, IPACache> CACHE = new ConcurrentHashMap<>();

    private static IPACache of(Dialect d)
    {
        return CACHE.computeIfAbsent(d, i -> new IPACache(d));
    }

    private final Map<Quadruple<IPinyin, DictCode, IPASyllStyle, IPAToneStyle>, String> data;

    private IPACache(Dialect d)
    {
        this.data = IPAService.getData(d);
    }

    public final void reload(Dialect d)
    {
        CACHE.remove(d);
        CACHE.put(d, new IPACache(d));
    }

    public static Maybe<String> get(IPinyin pinyin, DictCode dict, PinyinConfig op)
    {
        // 通过Dialect获取实例
        // 通过四个关键词 IPinyin DictCode IPASyllStyle IPAToneStyle 查找
        // 如果有的话，在两边打[]
        return Maybe.uncertain(of(op.getDialect()).data.get(
                Quadruple.of(pinyin, dict, op.getSyllStyle(), op.getToneStyle())
        )).handleIfExist(str -> String.format("[%s]", str));
    }
}
