package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.dataStructure.functions.TriFunction;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;

import java.util.*;
import java.util.function.Function;

/**
 * 国际音标查询结果集
 *
 * @implNote 使用控制反转查询，主要处理查询内容和结果集
 */

public class IPAData
{
    private final Set<String> source = new HashSet<>();
    private Map<String, Map<String, String>> data = null;

    public IPAData()
    {
    }

    public void add(Object input)
    {
        if (input == null) return;

        if (input instanceof String s) source.add(s);
        else if (input instanceof Collection<?> c)
        {
            for (Object item : c)
                source.add(String.valueOf(item));
        }
        else if (input.getClass().isArray())
        {
            int len = java.lang.reflect.Array.getLength(input);
            for (int i = 0; i < len; i++)
            {
                Object item = java.lang.reflect.Array.get(input, i);
                source.add(String.valueOf(item));
            }
        }
        else source.add(String.valueOf(input));
    }

    public void addAll(Object... inputs)
    {
        for (Object input : inputs) add(input);
    }

    public <T extends UniPinyin<U>, U extends PinyinStyle>
    void search(Dialect d, PinyinOption op,
                TriFunction<Set<T>, PinyinOption, Dialect, Map<T, Map<String, String>>> ipaSE)
    {
        if (data == null) data = new HashMap<>();

        Set<T> pySet = new HashSet<>();
        Function<String, T> pinyinFactory = d.getFactory();
        for (String i : source) pySet.add(pinyinFactory.apply(i));


        Map<T, Map<String, String>> metadata = ipaSE.apply(pySet, op, d);
        for (String i : source) data.put(i, metadata.get(pinyinFactory.apply(i)));
    }

    public String get(String pinyin, String dict)
    {
        if (data == null) throw new NullPointerException("没有经过查询流程");
        try
        {
            return data.get(pinyin).get(dict);
        } catch (Exception e)
        {
            return "暂无";
        }
    }

    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    IPAData getDirectly(Dialect d, PinyinOption op,
                        TriFunction<Set<T>, PinyinOption, Dialect, Map<T, Map<String, String>>> ipaSE,
                        Object... inputs)
    {
        IPAData data = new IPAData();
        data.addAll(inputs);
        data.search(d, op, ipaSE);
        return data;
    }
}
