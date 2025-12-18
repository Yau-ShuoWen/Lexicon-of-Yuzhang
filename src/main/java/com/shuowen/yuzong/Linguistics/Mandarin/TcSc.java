package com.shuowen.yuzong.Linguistics.Mandarin;

import com.hankcs.hanlp.HanLP;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;

import java.util.*;

import static com.shuowen.yuzong.Tool.JavaUtilExtend.MapTool.renameKey;

public class TcSc
{
    public static String t2s(String text)
    {
        return HanLP.t2s(text).
                replace("“", "「").
                replace("”", "」");
    }

    public static <V> void tagTrim(Map<String, V> map, Language lang, String tag)
    {
        String l = lang.toString();
        String r = lang.reverse().toString();
        map.remove(r);
        renameKey(map, l, tag);
    }
}
