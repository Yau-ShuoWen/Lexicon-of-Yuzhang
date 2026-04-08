package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;

public class IPAFormatter
{
    /**
     * 对着字符串，直接拼接就好了
     */
    public static String mergeFiveDegree(String syllable, String tone, boolean num)
    {
        if (num) return String.format("[%s%s]", syllable, mergeFiveDegreeNum(tone, false));
        else
        {
            // 这个做法请查看文档：国际音标类的描述
            if (tone.length() == 2 && tone.charAt(0) == tone.charAt(1))
                tone = tone + tone.charAt(1);

            if (tone.equals("0")) return "[·" + syllable + "]";
            else return "[" + syllable + "-" + (tone
                    .replace('1', '˩')
                    .replace('2', '˨')
                    .replace('3', '˧')
                    .replace('4', '˦')
                    .replace('5', '˥'))
                    + "]";
        }
    }

    /**
     * 区分大字小字
     */
    public static String mergeFiveDegreeNum(String tone, boolean capital)
    {
        if (capital) return tone.replaceAll("[꜈꜉꜊꜋꜌]", "0");
        else
        {
            return tone.replaceAll("[꜈꜉꜊꜋꜌0]", "⁰")// 这里是因为允许直接存特殊轻声符号，所以这里要替换回来
                    .replace('1', '¹')
                    .replace('2', '²')
                    .replace('3', '³')
                    .replace('4', '⁴')
                    .replace('5', '⁵');
        }
    }

    /**
     * 传入音节，四角类声调和词典，返回 ꜁tsɨn 形似的国际音标，四角标注圈法
     *
     * @param d 不是音调数字，而是数字调
     */
    public static String mergeFourCorner(String y, int d)
    {
        // 阴平 阳平 阴上 阳上 阴去 阳去 阴入 阳入 下阴入（广州话） 下阳入（南宁话）
        char[] marks = {' ', '꜀', '꜁', '꜂', '꜃', '꜄', '꜅', '꜆', '꜇', '꜀', '꜁'};
        char[] places = {' ', 'l', 'l', 'l', 'l', 'r', 'r', 'r', 'r', 'r', 'r'};

        // l在左边，r在右边
        if (NumberTool.closeBetween(d, 1, 10))
        {
            char mark = marks[d], place = places[d];
            return (place == 'l') ?
                    String.format("[%s]", mark + y) :
                    String.format("[%s]", y + mark);
        }
        else return String.format("[%s]", y); // 轻声直接返回
    }


    /**
     * 有9个音标和1个送气符号在汉语言学界之中通用，但却未能被国际音标接受。
     * <p>
     * 供复制测试字体用的{@code ɿ  ɹ̩  ʅ  ɻ̍  ʮ  ɹ̩ʷ  ʯ  ɻ̍ʷ  ȶ  t̠ʲ  ȡ  d̠ʲ  ȵ  ṉʲ  ᴀ  ä  ᴇ  e̞}
     */
    public static String formatSyllable(String s, IPASyllableStyle ss)
    {
        if (s == null) return null; //这里的null表示的是无效的拼音，是空安全的
        return switch (ss)
        {
            case CHINESE_SPECIAL -> s;
            case STANDARD_IPA -> s.
                    replace("'", "ʰ").
                    replace("ɿ", "ɹ̩").
                    replace("ʅ", "ɻ̍").
                    replace("ʮ", "ɹ̩ʷ").
                    replace("ʯ", "ɻ̍ʷ").
                    replace("ȶ", "tʲ").
                    replace("ȡ", "dʲ").
                    replace("ȵ", "ṉʲ").
                    replace("ᴀ", "ä").
                    replace("ᴇ", "e̞").
                    replace("ts", "t͡s").
                    replace("tɕ", "t͡ɕ").
                    replace("tʂ", "t͡ʂ").
                    replace("tʃ", "t͡ʃ").
                    replace("dz", "d͡z").
                    replace("dʑ", "d͡ʑ").
                    replace("dʐ", "d͡ʐ").
                    replace("dʒ", "d͡ʒ")
            ;
        };
    }
}