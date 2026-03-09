package com.shuowen.yuzong.Tool.TextTool;

import com.hankcs.hanlp.HanLP;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;

import java.util.*;

public class TcSc
{
    private TcSc()
    {
    }

    static String charset = """
            斗|鬥
            斗|斗
            干|干
            干|乾
            干|幹
            乾|乾
            """;

    static Map<String, List<String>> s2tMap = init(Language.SC);
    static Map<String, List<String>> t2sMap = init(Language.TC);


    private static Map<String, List<String>> init(Language from)
    {
        Map<String, List<String>> map = new HashMap<>();
        for (var i : charset.split("\n"))
        {
            var arr = i.split("\\|");
            String key, value;
            if (from.isSimplified())
            {
                key = arr[0];
                value = arr[1];
            }
            else
            {
                key = arr[1];
                value = arr[0];
            }
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
        return map;
    }

    public static List<String> t2s(String tc)
    {
        return t2sMap.getOrDefault(tc, List.of(HanLP.t2s(tc)));
    }

    public static List<String> s2t(String sc)
    {
        return s2tMap.getOrDefault(sc, List.of(HanLP.s2t(sc)));
    }
}
