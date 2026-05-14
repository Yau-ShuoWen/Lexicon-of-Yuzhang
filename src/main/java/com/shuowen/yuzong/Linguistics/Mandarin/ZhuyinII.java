package com.shuowen.yuzong.Linguistics.Mandarin;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;

import java.util.Map;

public class ZhuyinII
{
    public static RPinyin format(Zhuyin s)
    {
        String sy = zicisi(s);
        if (sy.isEmpty()) sy = common(s);

        return RPinyin.of(tone(sy, s));
    }

    private static String zicisi(Zhuyin s)
    {
        return switch (s.toStringWithoutTone())
        {
            case "ㄓ" -> "jr";
            case "ㄔ" -> "chr";
            case "ㄕ" -> "shr";
            case "ㄖ" -> "r";
            case "ㄗ" -> "tz";
            case "ㄘ" -> "tsz";
            case "ㄙ" -> "sz";
            default -> "";
        };
    }

    private static String common(Zhuyin s)
    {
        String Sheng, Yun;
        boolean zero = false;
        Sheng = switch (s.getInitial())
        {
            case "ㄅ" -> "b";
            case "ㄆ" -> "p";
            case "ㄇ" -> "m";
            case "ㄈ" -> "f";
            case "ㄉ" -> "d";
            case "ㄊ" -> "t";
            case "ㄋ" -> "n";
            case "ㄌ" -> "l";
            case "ㄍ" -> "g";
            case "ㄎ" -> "k";
            case "ㄏ" -> "h";
            case "ㄐ", "ㄓ" -> "j";
            case "ㄑ", "ㄔ" -> "ch";
            case "ㄒ", "ㄕ" -> "sh";
            case "ㄖ" -> "r";
            case "ㄗ" -> "tz";
            case "ㄘ" -> "ts";
            case "ㄙ" -> "s";
            default ->
            {
                zero = true;
                yield "";
            }
        };

        Yun = switch (s.getMiddle() + s.getLast())
        {
            case "ㄚ" -> "a";
            case "ㄛ" -> "o";
            case "ㄜ", "ㄝ" -> "e";
            case "ㄞ" -> "ai";
            case "ㄟ" -> "ei";
            case "ㄠ" -> "au";
            case "ㄡ" -> "ou";
            case "ㄢ" -> "an";
            case "ㄣ" -> "en";
            case "ㄤ" -> "ang";
            case "ㄥ" -> "eng";
            case "ㄦ" -> "er";

            case "ㄧ" -> (zero) ? "yi" : "i";
            case "ㄧㄚ" -> (zero) ? "ya" : "ia";
            case "ㄧㄛ" -> (zero) ? "yo" : "io";
            case "ㄧㄝ" -> (zero) ? "ye" : "ie";
            case "ㄧㄠ" -> (zero) ? "yau" : "iao";
            case "ㄧㄡ" -> (zero) ? "you" : "iou";
            case "ㄧㄢ" -> (zero) ? "yan" : "ian";
            case "ㄧㄣ" -> (zero) ? "yin" : "in";
            case "ㄧㄤ" -> (zero) ? "yang" : "iang";
            case "ㄧㄥ" -> (zero) ? "ying" : "ing";

            case "ㄨ" -> (zero) ? "wu" : "u";
            case "ㄨㄚ" -> (zero) ? "wa" : "ua";
            case "ㄨㄛ" -> (zero) ? "wo" : "uo";
            case "ㄨㄞ" -> (zero) ? "wai" : "uai";
            case "ㄨㄟ" -> (zero) ? "wei" : "uei";
            case "ㄨㄢ" -> (zero) ? "wan" : "uan";
            case "ㄨㄣ" -> (zero) ? "wen" : "uen";
            case "ㄨㄤ" -> (zero) ? "wang" : "uang";
            case "ㄨㄥ" -> (zero) ? "weng" : "ung";

            case "ㄩ" -> (zero) ? "yu" : "iu";
            case "ㄩㄝ" -> (zero) ? "yue" : "iue";
            case "ㄩㄢ" -> (zero) ? "yuan" : "iuan";
            case "ㄩㄣ" -> (zero) ? "yun" : "iun";
            case "ㄩㄥ" -> (zero) ? "yung" : "iung";

            default -> "";
        };

        return Sheng + Yun;
    }

    private static String tone(String syll, Zhuyin s)
    {
        Map<String, String[]> map = Map.of(
                "a", "aāáǎà".split(""),
                "o", "oōóǒò".split(""),
                "e", "eēéěè".split(""),
                "i", "iīíǐì".split(""),
                "u", "uūúǔù".split(""),
                "z", "z|z̄|ź|ž|z̀".split("\\|"),
                "r", "r|r̄|ŕ|ř|r̀".split("\\|")
        );
        for (String i : "aoeiuzr".split(""))
            if (syll.contains(i)) return syll.replace(i, map.get(i)[s.getTone()]);
        return syll;
    }
}