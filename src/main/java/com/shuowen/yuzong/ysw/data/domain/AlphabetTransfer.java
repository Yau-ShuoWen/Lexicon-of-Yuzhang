package com.shuowen.yuzong.ysw.data.domain;

import com.shuowen.yuzong.Linguistics.Mandarin.*;
import com.shuowen.yuzong.util.tuple.Range;
import com.shuowen.yuzong.ysw.linguistic.Alphabet;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.ysw.linguistic.MdrPYSceme;
import com.shuowen.yuzong.ysw.linguistic.Romatzyh;

/**
 * 各种转写的控制类
 */
public class AlphabetTransfer
{
    public static String format(Alphabet a, Language l, String funName, String s)
    {
        s = ScTcText.get(s, Language.SC).toString(); // 对繁体的支持不太好，会出现厦sha门这样的错误
        switch (a)
        {
            // 拼音
            case PinYin ->
            {
                var pys = HanPinyin.textPinyin(s);
                String ans = ""; ;
                for (var idx : Range.of(pys.size()))
                {
                    try
                    {
                        ans += pys.get(idx).getValue().getRead();
                    } catch (Exception e)
                    {
                        ans += String.format("%s", s.charAt(idx));
                    }
                }
                return ans.replace("]  [", " ");
            }

            // 注音
            case BoPoMoFo ->
            {
                var pys = HanPinyin.textPinyin(s);
                String ans = ""; ;
                for (var idx : Range.of(pys.size()))
                {
                    try
                    {
                        ans += String.format(" %s ", Zhuyin.tryOf(pys.get(idx).getValue()).getValue().toString());
                    } catch (Exception e)
                    {
                        ans += String.format("%s", s.charAt(idx));
                    }
                }
                return ans;
            }

            // 罗马字：单独处理
            case Romatzyh ->
            {
                // 唯一一个广泛接受的专有名词
                s = s
                        .replace("国语罗马字", " [GwoYeu Romatzyh] ")
                        .replace("國語羅馬字", " [GwoYeu Romatzyh] ")
                        .replace("罗马字", " [Romatzyh] ")
                        .replace("羅馬字", " [Romatzyh] ");

                var pys = HanPinyin.textPinyin(s);
                String ans = "";

                for (var idx : Range.of(pys.size()))
                {
                    try
                    {
                        ans += Romatzyh.format(Zhuyin.tryOf(pys.get(idx).getValue()).getValue());
                    } catch (Exception e)
                    {
                        ans += String.format("%s", s.charAt(idx));
                    }
                }
                return ans.replace("]  [", " ");
            }

            // 基于汉语拼音替换的拼音方案
            case Wade, ZhuyinII, TYPinyin ->
            {
                var tool = MdrPYSceme.of(a);

                var pys = HanPinyin.textPinyin(s);
                String ans = "";

                for (var idx : Range.of(pys.size()))
                {
                    try
                    {
                        ans += tool.format(Zhuyin.tryOf(pys.get(idx).getValue()).getValue());
                    } catch (Exception e)
                    {
                        ans += String.format("%s", s.charAt(idx));
                    }
                }
                return ans.replace("]  [", " ");
            }

            // 数字类
            case SuZhouCode, RomanNumber, NumberSystem ->
            {
                return NumberTransfer.format(a, l, funName, s);
            }

            default -> throw new RuntimeException("");
        }
    }
}

