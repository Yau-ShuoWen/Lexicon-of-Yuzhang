package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.linguistics.pinyinv2.PronunciationDictionary;
import com.shuowen.yuzong.linguistics.pinyinv2.UniPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 南昌话辞书的类型边界。
 * <p>
 * 子类只需要实现本辞书自己的转写规则，不需要重复检查拼音所属方言。
 */
public abstract class LACDictionary implements PronunciationDictionary
{
    @Override
    public final Dialect dialect()
    {
        return Dialect.LAC;
    }

    @Override
    public final Maybe<String> transcribe(
            UniPinyin pinyin,
            IPASyllStyle syllStyle,
            IPAToneStyle toneStyle
    )
    {
        if (!(pinyin instanceof LACPinyin lac))
            throw new IllegalArgumentException(code() + "只能处理南昌话拼音");
        return transcribe(lac, syllStyle, toneStyle);
    }

    protected abstract Maybe<String> transcribe(
            LACPinyin pinyin,
            IPASyllStyle syllStyle,
            IPAToneStyle toneStyle
    );

    protected String handleDandu(LACPinyin.DanDu dandu)
    {
        return switch (dandu)
        {
            case m -> "m̩";
            case n -> "n̩";
            case ŋ -> "ŋ̍";
        };
    }

    protected String handleShengMu(LACPinyin.ShengMu initial)
    {
        return switch (initial)
        {
            case $ -> "";
            case b -> "p";
            case p -> "p'";
            case m -> "m";
            case f -> "f";
            case d -> "t";
            case t -> "t'";
            case l -> "l";
            case g -> "k";
            case k -> "k'";
            case ŋ -> "ŋ";
            case h -> "h";
            case j -> "tɕ";
            case q -> "tɕ'";
            case n -> "ȵ";
            case x -> "ɕ";
            case z -> "ts";
            case c -> "ts'";
            case s -> "s";
        };
    }

    protected String handleJieMu(LACPinyin.JieMu jiemu)
    {
        return switch (jiemu)
        {
            case $ -> "";
            case i -> "i";
            case u -> "u";
            case ü -> "y";
        };
    }

    /**
     * 标准辞书中的韵尾实现；真正跨方言稳定的部分交给 IPACommon。
     */
    protected String handleYunWei(LACPinyin.YunWei yunwei)
    {
        return switch (yunwei)
        {
            case $ -> "";
            case i -> "i";
            case u -> "u";
            case n -> "n";
            case ŋ -> "ŋ";
            case t -> "t";
            case k -> "ʔ";
            case l -> "l";
        };
    }

    /**
     * 各辞书只提供调值；三种显示格式的机械转换统一放在这里。
     */
    protected String formatTone(
            String syllable,
            LACPinyin.YinDiao tone,
            IPAToneStyle style,
            String toneValue
    )
    {
        if (tone == LACPinyin.YinDiao.$) return syllable;
        int corner = switch (tone)
        {
            case t0 -> 0;
            case t1 -> 1;
            case t2 -> 2;
            case t3 -> 3;
            case t4 -> 5;
            case t5 -> 6;
            case t6 -> 7;
            case t7 -> 8;
            case $ -> throw new IllegalStateException("已经在前面流程中处理，请查看是否多余");
        };
        return switch (style)
        {
            case FIVE_DEGREE_LINE -> IPAFormatter.mergeFiveDegree(syllable, toneValue, false);
            case FIVE_DEGREE_NUM -> IPAFormatter.mergeFiveDegree(syllable, toneValue, true);
            case FOUR_CORNER -> IPAFormatter.mergeFourCorner(syllable, corner);
        };
    }

}
