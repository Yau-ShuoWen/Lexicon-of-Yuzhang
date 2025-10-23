package com.shuowen.yuzong.dao.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.dataStructure.functions.TriFunction;
import com.shuowen.yuzong.dao.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.dao.domain.IPA.IPAToneStyle;

import java.util.*;
import java.util.function.Function;

public class PinyinTool
{
    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    Map<String, String> formatPinyin(Set<String> py, U style, Function<String, T> creator)
    {
        Map<String, String> map = new HashMap<>();
        for (String i : py)
            map.put(i, creator.apply(i).toString(style));
        return map;
    }

    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    Map<String, Map<String, String>> formatIPA(Set<String> py, Function<String, T> creator,
                                               TriFunction<Set<T>, IPAToneStyle, IPASyllableStyle, Map<T, Map<String, String>>> ipaSE,
                                               IPAToneStyle ts, IPASyllableStyle ss)
    {
        Set<T> pySet = new HashSet<>();
        Map<String, Map<String, String>> res = new HashMap<>();

        for (String i : py) pySet.add(creator.apply(i));
        Map<T, Map<String, String>> data = ipaSE.apply(pySet, ts, ss);
        for (String i : py)
            res.put(i, data.get(creator.apply(i)));

        return res;
    }
}