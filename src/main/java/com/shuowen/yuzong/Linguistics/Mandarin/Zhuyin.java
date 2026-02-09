package com.shuowen.yuzong.Linguistics.Mandarin;

import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import lombok.Getter;

import static com.shuowen.yuzong.data.domain.Pinyin.PinyinFormatter.trySplit;

/**
 * 不可变汉语注音对象
 */
@Getter
public class Zhuyin
{
    final private String initial, middle, last;
    final private int tone;
    final private String code;
    final private String pinyin;

    public String toString()
    {
        String[] sidemark = {null, "", "ˊ", "ˇ", "ˋ"};

        return NumberTool.closeBetween(tone, 1, 4) ?
                initial + middle + last + sidemark[tone] :
                "·" + initial + middle + last;
    }

    public String toStringWithNumTone()
    {
        return initial + middle + last + tone;
    }

    public String toStringWithoutTone()
    {
        return initial + middle + last;
    }

    public static Maybe<Zhuyin> tryOf(String pinyin)
    {
        try
        {
            return Maybe.exist(new Zhuyin(pinyin));
        } catch (InvalidPinyinException e)
        {
            return Maybe.nothing();
        }
    }

    public static Zhuyin of(String pinyin)
    {
        var maybe = tryOf(pinyin);
        if (maybe.isEmpty()) throw new InvalidPinyinException("初始化失败");
        return maybe.getValue();
    }

    private Zhuyin(String text)
    {
        var tmp = trySplit(text);
        var syllablePinyin = tmp.getLeft();  // 拼音的音节
        var lastPinyin = tmp.getRight();     // 拼音的音调

        var syllable = initSyllable(syllablePinyin);
        initial = syllable.getLeft();
        middle = syllable.getMiddle();
        last = syllable.getRight();

        tone = initTone(lastPinyin);
        code = initCode();
        pinyin = toPinyin();

        // 这里比较的是按照解析后数据重新构造的拼音是否正确
        if (!pinyin.equals(syllablePinyin + lastPinyin))
            throw new InvalidPinyinException("校验失败，拼音无效");
    }


    private Integer initTone(Integer tone)
    {
        if (!NumberTool.closeBetween(tone, 0, 4))
            throw new InvalidPinyinException("音调超出范围");
        return tone;
    }

    private Triple<String, String, String> initSyllable(String str)
    {
        var ans = Triple.of("", "", "");

        // 儿化音单独处理
        if ("er".equals(str)) return Triple.of("", "", "ㄦ");

        str = str.replace("v", "ü");

        int idx = 0;

        ans.setLeft(switch (str.charAt(idx))
        {
            case 'b' -> "ㄅ";
            case 'p' -> "ㄆ";
            case 'm' -> "ㄇ";
            case 'f' -> "ㄈ";
            case 'd' -> "ㄉ";
            case 't' -> "ㄊ";
            case 'n' -> "ㄋ";
            case 'l' -> "ㄌ";
            case 'g' -> "ㄍ";
            case 'k' -> "ㄎ";
            case 'h' -> "ㄏ";
            case 'j' ->
            {
                if (StringTool.charEquals(str, idx + 1, 'u'))
                    str = str.replace("u", "ü");
                yield "ㄐ";
            }
            case 'q' ->
            {
                if (StringTool.charEquals(str, idx + 1, 'u'))
                    str = str.replace("u", "ü");
                yield "ㄑ";
            }
            case 'x' ->
            {
                if (StringTool.charEquals(str, idx + 1, 'u'))
                    str = str.replace("u", "ü");
                yield "ㄒ";
            }
            case 'r' ->
            {
                if (StringTool.charEquals(str, idx + 1, 'i')) idx++;
                yield "ㄖ";
            }
            case 'z' ->
            {
                String tmp;
                if (StringTool.charEquals(str, idx + 1, 'h'))
                {
                    tmp = "ㄓ";
                    idx++;
                }
                else tmp = "ㄗ";

                if (StringTool.charEquals(str, idx + 1, 'i')) idx++;
                yield tmp;
            }
            case 'c' ->
            {
                String tmp;
                if (StringTool.charEquals(str, idx + 1, 'h'))
                {
                    tmp = "ㄔ";
                    idx++;
                }
                else tmp = "ㄘ";

                if (StringTool.charEquals(str, idx + 1, 'i')) idx++;
                yield tmp;
            }
            case 's' ->
            {
                String tmp;
                if (StringTool.charEquals(str, idx + 1, 'h'))
                {
                    tmp = "ㄕ";
                    idx++;
                }
                else tmp = "ㄙ";

                if (StringTool.charEquals(str, idx + 1, 'i')) idx++;
                yield tmp;
            }
            case 'y' ->
            {
                str = str.replace("yu", "ü")
                        .replace("yi", "i")
                        .replace("y", "i");
                idx--;
                yield "";
            }
            case 'w' ->
            {
                str = str.replace("wu", "u")
                        .replace("w", "u");
                idx--;
                yield "";
            }
            default ->
            {
                idx--;
                yield "";
            }
        });
        idx++;
        str = str.substring(idx);
        if (str.isEmpty()) return ans;

        str = str.replace("ie", "iê")
                .replace("iu", "iou")
                .replace("in", "ien")
                .replace("ing", "ieng")
                .replace("ui", "uei")
                .replace("un", "uen")
                .replace("üe", "üê")
                .replace("ün", "üen")
                .replace("iong", "üeng")
                .replace("ong", "ueng");

        idx = 0;
        ans.setMiddle(switch (str.charAt(idx))
        {
            case 'i' -> "ㄧ";
            case 'u' -> "ㄨ";
            case 'ü' -> "ㄩ";
            default ->
            {
                idx--;
                yield "";
            }
        });
        idx++;
        str = str.substring(idx);
        if (str.isEmpty()) return ans;

        ans.setRight(switch (str)
        {
            case "a" -> "ㄚ";
            case "o" -> "ㄛ";
            case "e" -> "ㄜ";
            case "ê" -> "ㄝ";
            case "ai" -> "ㄞ";
            case "ei" -> "ㄟ";
            case "ao" -> "ㄠ";
            case "ou" -> "ㄡ";
            case "an" -> "ㄢ";
            case "en" -> "ㄣ";
            case "ang" -> "ㄤ";
            case "eng" -> "ㄥ";
            default -> throw new InvalidPinyinException("匹配结束，却仍然有剩余内容");
        });

        return ans;
    }

    private String initCode()
    {
        return switch (initial)
        {
            case "ㄅ" -> "01";
            case "ㄆ" -> "02";
            case "ㄇ" -> "03";
            case "ㄈ" -> "04";
            case "ㄉ" -> "05";
            case "ㄊ" -> "06";
            case "ㄋ" -> "07";
            case "ㄌ" -> "08";
            case "ㄍ" -> "09";
            case "ㄎ" -> "10";
            case "ㄏ" -> "11";
            case "ㄐ" -> "12";
            case "ㄑ" -> "13";
            case "ㄒ" -> "14";
            case "ㄓ" -> "15";
            case "ㄔ" -> "16";
            case "ㄕ" -> "17";
            case "ㄖ" -> "18";
            case "ㄗ" -> "19";
            case "ㄘ" -> "20";
            case "ㄙ" -> "21";
            default -> "00";
        } + switch (middle)
        {
            case "ㄧ" -> "1";
            case "ㄨ" -> "2";
            case "ㄩ" -> "3";
            default -> "0";
        } + switch (last)
        {
            case "ㄚ" -> "01";
            case "ㄛ" -> "02";
            case "ㄜ" -> "03";
            case "ㄝ" -> "04";
            case "ㄞ" -> "05";
            case "ㄟ" -> "06";
            case "ㄠ" -> "07";
            case "ㄡ" -> "08";
            case "ㄢ" -> "09";
            case "ㄣ" -> "10";
            case "ㄤ" -> "11";
            case "ㄥ" -> "12";
            case "ㄦ" -> "13";
            default -> "00";
        };
    }

    private String toPinyin()
    {
        boolean zero = false;  //零声母
        String sheng = switch (initial)
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
            case "ㄑ" -> "q";
            case "ㄒ" -> "x";
            case "ㄓ" -> "zh";
            case "ㄔ" -> "ch";
            case "ㄕ" -> "sh";
            case "ㄖ" -> "r";
            case "ㄗ" -> "z";
            case "ㄘ" -> "c";
            case "ㄙ" -> "s";
            default ->
            {
                zero = true;
                yield "";
            }
        };

        String yun = switch (middle + last)
        {
            case "ㄚ" -> "a";
            case "ㄛ" -> "o";
            case "ㄜ" -> "e";
            case "ㄞ" -> "ai";
            case "ㄟ" -> "ei";
            case "ㄠ" -> "ao";
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
            case "ㄧㄡ" -> (zero) ? "you" : "iu";
            case "ㄧㄢ" -> (zero) ? "yan" : "ian";
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
            case "ㄨㄥ" -> (zero) ? "weng" : "ong";

            case "ㄩ" -> (zero) ? "yu" : "ü";
            case "ㄩㄝ" -> (zero) ? "yue" : "üe";
            case "ㄩㄢ" -> (zero) ? "yuan" : "üan";
            case "ㄩㄣ" -> (zero) ? "yun" : "ün";
            case "ㄩㄥ" -> (zero) ? "yong" : "iong";

            case "" -> "i";//如果没有任何声母，那说明是zhi chi shi ri zi ci si
            default -> "";
        };
        String ans = (sheng + yun);
        return ans.replace("ü", (ans.matches("[jqx]ü[a-z]*")) ? "u" : "v") + tone;
    }
}
