package com.shuowen.yuzong.Linguistics.Mandarin;

import com.hankcs.hanlp.HanLP;

public class TcSc
{
    public static String t2s(String text)
    {
        return HanLP.t2s(text).
                replace("“", "「").
                replace("”", "」");
    }
}
