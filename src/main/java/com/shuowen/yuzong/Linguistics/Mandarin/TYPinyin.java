package com.shuowen.yuzong.Linguistics.Mandarin;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;

import java.util.Map;

public class TYPinyin
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
            case "ㄓ" -> "jhih";
            case "ㄔ" -> "chih";
            case "ㄕ" -> "shih";
            case "ㄖ" -> "rih";
            case "ㄗ" -> "zih";
            case "ㄘ" -> "cih";
            case "ㄙ" -> "sih";
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
            case "ㄐ" -> "j";
            case "ㄑ", "ㄘ" -> "c";
            case "ㄒ", "ㄙ" -> "s";
            case "ㄓ" -> "jh";
            case "ㄔ" -> "ch";
            case "ㄕ" -> "sh";
            case "ㄖ" -> "r";
            case "ㄗ" -> "z";
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
            case "ㄧㄠ" -> (zero) ? "yao" : "iao";
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
            case "ㄨㄣ" -> (zero) ? "wun" : "un";
            case "ㄨㄤ" -> (zero) ? "wang" : "uang";
            case "ㄨㄥ" -> (zero) ? "wong" : "ong";

            case "ㄩ" -> "yu";
            case "ㄩㄝ" -> "yue";
            case "ㄩㄢ" -> "yuan";
            case "ㄩㄣ" -> "yun";
            case "ㄩㄥ" -> "yong";

            default -> "";
        };

        String ans= Sheng + Yun;
        if(ans.equals("feng")) ans="fong";
        return ans;
    }

    private static String tone(String syll, Zhuyin s)
    {
        Map<String, String[]> map = Map.of(
                "a", "å|ā|á|ǎ|à".split("\\|"),
                "o", "o̊|ō|ó|ǒ|ò".split("\\|"),
                "e", "e̊|ē|é|ě|è".split("\\|"),
                "i", "i̊|ī|í|ǐ|ì".split("\\|"),
                "u", "ů|ū|ú|ǔ|ù".split("\\|")
        );
        for (String i : "aoeiuü".split(""))
            if (syll.contains(i)) return syll.replace(i, map.get(i)[s.getTone()]);
        return syll;
    }
}
