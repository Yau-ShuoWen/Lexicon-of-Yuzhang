package com.shuowen.yuzong.data.domain.Personal;

import com.shuowen.yuzong.Linguistics.Mandarin.*;
import com.shuowen.yuzong.Tool.dataStructure.Range;
import com.shuowen.yuzong.Tool.dataStructure.option.Alphabet;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;

public class AlphabetTransfer
{
    public static String format(Alphabet a, String s)
    {
        s = ScTcText.get(s, Language.SC).toString(); // 对繁体的支持不太好，会出现厦sha门这样的错误
        switch (a)
        {
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
            case ZhuYin ->
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

            default -> throw new RuntimeException("");
        }
    }
}

