package com.shuowen.yuzong.Linguistics.Mandarin;

import com.shuowen.yuzong.Linguistics.Scheme.PinyinCommon;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;

public class WadePinyin
{
    private WadePinyin()
    {
    }

    public static RPinyin format(Zhuyin s)
    {
        s = preHandle(s);

        String sy = zicisi(s);
        if (sy.isEmpty()) sy = common(s);

        char tone = PinyinCommon.toSuperScript(s.getTone());

        return RPinyin.of(sy + tone);
    }

    private static Zhuyin preHandle(Zhuyin s)
    {
        return switch (s.toStringWithoutTone())
        {
            case "ㄍㄜ" -> Zhuyin.of("go" + s.getTone());
            case "ㄎㄜ" -> Zhuyin.of("ko" + s.getTone());
            case "ㄏㄜ" -> Zhuyin.of("ho" + s.getTone());
            case "ㄉㄨㄛ" -> Zhuyin.of("do" + s.getTone());
            case "ㄊㄨㄛ" -> Zhuyin.of("to" + s.getTone());
            case "ㄋㄨㄛ" -> Zhuyin.of("no" + s.getTone());
            case "ㄌㄨㄛ" -> Zhuyin.of("lo" + s.getTone());
            case "ㄓㄨㄛ" -> Zhuyin.of("zho" + s.getTone());
            case "ㄔㄨㄛ" -> Zhuyin.of("cho" + s.getTone());
            case "ㄕㄨㄛ" -> Zhuyin.of("sho" + s.getTone());
            case "ㄖㄨㄛ" -> Zhuyin.of("ro" + s.getTone());
            case "ㄗㄨㄛ" -> Zhuyin.of("zo" + s.getTone());
            case "ㄘㄨㄛ" -> Zhuyin.of("co" + s.getTone());
            case "ㄙㄨㄛ" -> Zhuyin.of("so" + s.getTone());

            default -> s;
        };
    }

    private static String zicisi(Zhuyin s)
    {
        return switch (s.toStringWithoutTone())
        {
            case "ㄓ" -> "chih";
            case "ㄔ" -> "chʻih";
            case "ㄕ" -> "shih";
            case "ㄖ" -> "jih";
            case "ㄗ" -> "tzŭ";
            case "ㄘ" -> "tzʻŭ";
            case "ㄙ" -> "ssŭ";
            default -> "";
        };
    }

    private static String common(Zhuyin s)
    {
        String Sheng, Yun;
        boolean zero = false;
        Sheng = switch (s.getInitial())
        {
            case "ㄅ" -> "p";
            case "ㄆ" -> "pʻ";
            case "ㄇ" -> "m";
            case "ㄈ" -> "f";
            case "ㄉ" -> "t";
            case "ㄊ" -> "tʻ";
            case "ㄋ" -> "n";
            case "ㄌ" -> "l";
            case "ㄍ" -> "k";
            case "ㄎ" -> "kʻ";
            case "ㄏ" -> "h";
            case "ㄐ", "ㄓ" -> "ch";
            case "ㄑ", "ㄔ" -> "chʻ";
            case "ㄒ" -> "hs";
            case "ㄕ" -> "sh";
            case "ㄖ" -> "j";
            case "ㄗ" -> "ts";
            case "ㄘ" -> "tsʻ";
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
            case "ㄜ" -> "ê";
            case "ㄝ" -> "eh";
            case "ㄞ" -> "ai";
            case "ㄟ" -> "ei";
            case "ㄠ" -> "ao";
            case "ㄡ" -> "ou";
            case "ㄢ" -> "an";
            case "ㄣ" -> "ên";
            case "ㄤ" -> "ang";
            case "ㄥ" -> "êng";
            case "ㄦ" -> "êrh";

            case "ㄧ" -> "i";
            case "ㄧㄚ" -> (zero) ? "ya" : "ia";
            case "ㄧㄛ" -> (zero) ? "yo" : "io";
            case "ㄧㄝ" -> (zero) ? "yeh" : "ieh";
            case "ㄧㄠ" -> (zero) ? "yao" : "iao";
            case "ㄧㄡ" -> (zero) ? "yu" : "iu";
            case "ㄧㄢ" -> (zero) ? "yen" : "ien";
            case "ㄧㄣ" -> (zero) ? "yin" : "in";
            case "ㄧㄤ" -> (zero) ? "yang" : "iang";
            case "ㄧㄥ" -> (zero) ? "ying" : "ing";

            case "ㄨ" -> (zero) ? "wu" : "u";
            case "ㄨㄚ" -> (zero) ? "wa" : "ua";
            case "ㄨㄛ" -> (zero) ? "wo" : "uo";
            case "ㄨㄞ" -> (zero) ? "wai" : "uai";
            case "ㄨㄟ" -> (zero) ? "wei" : "ui";
            case "ㄨㄢ" -> (zero) ? "wan" : "uan";
            case "ㄨㄣ" -> (zero) ? "wen" : "un";
            case "ㄨㄤ" -> (zero) ? "wang" : "uang";
            case "ㄨㄥ" -> (zero) ? "wêng" : "ung";

            case "ㄩ" -> (zero) ? "yü" : "ü";
            case "ㄩㄝ" -> (zero) ? "yüeh" : "üeh";
            case "ㄩㄢ" -> (zero) ? "yüan" : "üan";
            case "ㄩㄣ" -> (zero) ? "yün" : "ün";
            case "ㄩㄥ" -> (zero) ? "yung" : "iung";

            default -> "";
        };

        return Sheng + Yun;
    }
}
