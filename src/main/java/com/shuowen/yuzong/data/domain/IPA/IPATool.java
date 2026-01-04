package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Linguistics.Scheme.Pinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;

import java.util.*;

public class IPATool
{
    public static Map<String, String> mergeAPI(
            Maybe<Yinjie> yinjieData, Maybe<Shengdiao> shengdiaoData,
            Pinyin p, PinyinOption op, Set<String> dict)
    {
        // 对于查询结果，如果音节和音调有一个是无效的，就查不出结果，直接返回
        if (yinjieData.isEmpty() || shengdiaoData.isEmpty()) return Map.of();
        var y = yinjieData.getValue();
        var s = shengdiaoData.getValue();

        Map<String, String> ans = new HashMap<>();
        for (var i : dict)
        {
            // 就算上一次查询到了结果，对于字典也不一定有结果
            // 如果查不到结果，那么对于这个字典的这个读音就是无效的，直接略过
            var yjInfo = y.getInfo(i);
            var sdInfo = s.getInfo(i);
            if (yjInfo.isEmpty() || sdInfo.isEmpty()) continue;

            var yj = yjInfo.getValue();
            var sd = sdInfo.getValue();

            var tmp = switch (op.getTone())
            {
                case FIVE_DEGREE_NUM -> mergeFiveDegree(yj, sd, true);
                case FIVE_DEGREE_LINE -> mergeFiveDegree(yj, sd, false);
                case FOUR_CORNER -> mergeFourCorner(yj, p.getCorner());
            };
            ans.put(i, formatSyllable(tmp, op.getSyllable()));
        }
        return ans;
    }


    /**
     * 对着字符串，直接拼接就好了
     */
    public static String mergeFiveDegree(String syllable, String tone, boolean num)
    {
        if (num)
        {
            return "[" + syllable + (tone
                    .replaceAll("[꜈꜉꜊꜋꜌0]", "⁰")// 这里是因为允许直接存特殊轻声符号，所以这里要替换回来
                    .replace('1', '¹')
                    .replace('2', '²')
                    .replace('3', '³')
                    .replace('4', '⁴')
                    .replace('5', '⁵')
            ) + "]";
        }
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
     * 传入音节，四角类声调和词典，返回 ꜁tsɨn 形似的国际音标
     *
     * @param d 不是音调数字，而是数字调
     */
    private static String mergeFourCorner(String y, int d)
    {
        String[] fourCornerMark = {"", "꜀", "꜁", "꜂", "꜃", "꜄", "꜅", "꜆", "꜇"};
        boolean[] fourCornerPlace = {false, true, true, true, true, false, false, false, false};
        // true：在左边，false在右边
        if (NumberTool.closeBetween(d, 1, 8))
        {
            return "[" + (fourCornerPlace[d] ? fourCornerMark[d] + y : y + fourCornerMark[d]) + "]";
        }
        else return y; // 轻声直接返回
    }


    /**
     * 有9个音标和1个送气符号在汉语言学界之中通用，但却未能被国际音标接受。
     * <p>
     * 供复制测试字体用的{@code ɿ  ɹ̩  ʅ  ɻ̍  ʮ  ɹ̩ʷ  ʯ  ɻ̍ʷ  ȶ  t̠ʲ  ȡ  d̠ʲ  ȵ  ṉʲ  ᴀ  ä  ᴇ  e̞}
     */
    private static String formatSyllable(String s, IPASyllableStyle ss)
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
                    replace("ȶ", "t̠ʲ").
                    replace("ȡ", "d̠ʲ").
                    replace("ȵ", "ṉʲ").
                    replace("ᴀ", "ä").
                    replace("ᴇ", "e̞");
        };
        // TODO：这里理论上要加这个东西，但是这是严式音标的内容而不是音标转换的内容
        //  replace("ts","t͡s").
        //  replace("tɕ","t͡ɕ").
        //  replace("tʂ","t͡ʂ").
    }
}
