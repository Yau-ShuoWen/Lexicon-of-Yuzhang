package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.dict.data.domain.IPA.IPA;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Either;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 南昌话拼音自身采用的默认 IPA 方案，是字典认为的
 */
public final class Dict_Standard extends LACDictionary
{
    public static final Dict_Standard INSTANCE = new Dict_Standard();

    private Dict_Standard()
    {
    }

    @Override
    public DictCode code()
    {
        return DictCode.STANDARD;
    }

    @Override
    protected Maybe<IPA> transcribe(LACPinyin pinyin, IPASyllStyle syllStyle, IPAToneStyle toneStyle)
    {
        String raw = pinyin.yinjieOrDandu.fold(this::handleDandu, this::handleYinjie);
        String syllable = IPAFormatter.formatSyllable(raw, syllStyle);

        LACPinyin.YinDiao tone = pinyin.yindiao.fold(
                value -> value,
                value -> switch (value)
                {
                    case t33 -> LACPinyin.YinDiao.t3;
                    case t55 -> LACPinyin.YinDiao.t5;
                    case t77 -> LACPinyin.YinDiao.t7;
                }
        );
        return Maybe.exist(new IPA(formatTone(syllable, tone, toneStyle, handleYinDiao(pinyin.yindiao))));
    }

    private String handleYinjie(LACPinyin.YinJie yinjie)
    {
        return handleShengMu(yinjie.getShengmu(), yinjie.getJiemu()) +
                handleJieMu(yinjie.getJiemu()) +
                handleYunMu(yinjie.getYunmu(), yinjie.getYunwei()) +
                handleYunWei(yinjie.getYunwei());
    }

    private String handleShengMu(LACPinyin.ShengMu shengmu, LACPinyin.JieMu jiemu)
    {
        return switch (shengmu)
        {
            case f -> "ɸ";
            case n -> switch (jiemu)
            {
                case i, ü -> "ȵ";
                case $, u -> "n";
            };
            default -> handleShengMu(shengmu);
        };
    }

    private String handleYunMu(LACPinyin.YunMu yunmu, LACPinyin.YunWei yunwei)
    {
        return switch (yunmu)
        {
            case $ -> "";
            case ı -> "ɿ";
            case a -> "a";
            case o -> switch (yunwei)
            {
                case ŋ, k -> "ɔ";
                default -> "o";
            };
            case e -> switch (yunwei)
            {
                case u, n, t -> "ε";
                default -> "e";
            };
            case ẹ -> "ə";
            case ọ -> "ɵ";
            case u -> "u";
        };
    }

    @Override
    protected String handleYunWei(LACPinyin.YunWei yunwei)
    {
        return switch (yunwei)
        {
            case t, l -> "ɨʔ";
            default -> super.handleYunWei(yunwei);
        };
    }

    private String handleYinDiao(Either<LACPinyin.YinDiao, LACPinyin.BianDiao> tone)
    {
        if (tone.isLeft())
        {
            return switch (tone.getLeft())
            {
                case $ -> "";
                case t0 -> "0";
                case t1 -> "42";
                case t2 -> "24";
                case t3 -> "213";
                case t4 -> "45";
                case t5 -> "21";
                case t6 -> "5";
                case t7 -> "2";
            };
        }
        else
        {
            return switch (tone.getRight())
            {
                case t33, t77, t55 -> "213$2$4";
            };
        }
    }
}
