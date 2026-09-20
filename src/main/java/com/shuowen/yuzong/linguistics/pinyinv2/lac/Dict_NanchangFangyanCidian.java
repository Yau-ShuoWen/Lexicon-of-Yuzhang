package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/** 《南昌方言词典》的记音规则。 */
public final class Dict_NanchangFangyanCidian extends LACDictionary
{
    public static final Dict_NanchangFangyanCidian INSTANCE = new Dict_NanchangFangyanCidian();

    private Dict_NanchangFangyanCidian()
    {
    }

    @Override
    public DictCode code()
    {
        return DictCode.NCDICT;
    }

    @Override
    protected Maybe<String> transcribe(
            LACPinyin pinyin,
            IPASyllStyle syllStyle,
            IPAToneStyle toneStyle
    )
    {
        Maybe<String> rawSyllable = pinyin.yinjie.fold(
                dandu -> value(handleDandu(dandu)),
                this::syllable
        );
        if (rawSyllable.isEmpty()) return Maybe.nothing();

        String syllable = IPAFormatter.formatSyllable(rawSyllable.getValue(), syllStyle);
        return pinyin.yindiao.fold(
                tone -> applyTone(syllable, tone, toneStyle),
                ignored -> Maybe.nothing()
        );
    }

    private Maybe<String> syllable(LACPinyin.YinJie syllable)
    {
        if (!supports(syllable)) return Maybe.nothing();
        return Maybe.exist(handleShengMu(syllable.getShengmu()) + handleYun(syllable));
    }

    // Dandu  ：标准实现
    // ShengMu：标准实现
    // JieMu  ：标准实现

    String handleYunMu(LACPinyin.YunMu yunmu, LACPinyin.YunWei yunwei)
    {
        return switch (yunmu)
        {
            case $ -> "";
            case ı -> "ɿ";
            case a -> "a";
            case o -> yunwei == LACPinyin.YunWei.ŋ || yunwei == LACPinyin.YunWei.k ? "ɔ" : "o";
            case e -> switch (yunwei)
            {
                case u, n, t -> "ε";
                default -> "e";
            };
            case ẹ -> "ɨ";
            case ọ -> "ɵ";
            case u -> "u";
        };
    }

    String handleYun(LACPinyin.YinJie yinjie)
    {
        return handleJieMu(yinjie.getJiemu()) +
                handleYunMu(yinjie.getYunmu(), yinjie.getYunwei()) +
                handleYunWei(yinjie.getYunwei());
    }

    /**
     * 收录范围和音值计算分开：这里只回答本辞书有没有这个韵母。
     */
    boolean supports(LACPinyin.YinJie syllable)
    {
        return switch (syllable.getYun())
        {
            case "a", "o", "e", "i", "ia", "ie", "u", "ua", "uo", "ue",
                 "ü", "üe", "ai", "oi", "ei", "ui", "uai", "au", "iu",
                 "an", "on", "in", "un", "uan", "uon", "ün", "üon",
                 "aŋ", "iaŋ", "iuŋ", "uŋ", "uaŋ", "at", "ot", "it", "ut",
                 "uat", "uot", "üt", "üot", "ọ", "ı", "ẹi", "eu", "ẹu",
                 "ieu", "en", "ẹn", "ien", "oŋ", "ioŋ", "uoŋ", "et", "ẹt",
                 "iet", "uet", "ak", "ok", "iak", "iok", "iuk", "uk", "uak",
                 "uok" -> true;
            default -> false;
        };
    }

    Maybe<String> applyTone(
            String syllable,
            LACPinyin.YinDiao tone,
            IPAToneStyle style
    )
    {
        if (tone == LACPinyin.YinDiao.$) return Maybe.exist(syllable);

        String value = switch (style)
        {
            case FIVE_DEGREE_LINE -> switch (tone)
            {
                case t0 -> "·" + syllable + "_";
                case t1 -> syllable + "_˦˨";
                case t2 -> syllable + "_˨˦";
                case t3 -> syllable + "_˨˩˧";
                case t4 -> syllable + "_˧˥";
                case t5 -> syllable + "_˩˩˩";
                case t6 -> syllable + "_˥";
                case t7 -> syllable + "_˨";
                case $ -> syllable;
            };
            case FIVE_DEGREE_NUM -> switch (tone)
            {
                case t0 -> syllable + "⁰";
                case t1 -> syllable + "⁴²";
                case t2 -> syllable + "²⁴";
                case t3 -> syllable + "²¹³";
                case t4 -> syllable + "³⁵";
                case t5 -> syllable + "¹¹";
                case t6 -> syllable + "⁵";
                case t7 -> syllable + "²";
                case $ -> syllable;
            };
            case FOUR_CORNER -> switch (tone)
            {
                case t0, $ -> syllable;
                case t1 -> "꜀" + syllable;
                case t2 -> "꜁" + syllable;
                case t3 -> "꜂" + syllable;
                case t4 -> syllable + "꜄";
                case t5 -> syllable + "꜅";
                case t6 -> syllable + "꜆";
                case t7 -> syllable + "꜇";
            };
        };
        return Maybe.exist(value);
    }

    private Maybe<String> value(String value)
    {
        return value == null ? Maybe.nothing() : Maybe.exist(value);
    }
}
