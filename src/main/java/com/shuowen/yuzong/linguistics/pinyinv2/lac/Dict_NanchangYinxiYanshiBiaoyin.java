package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/** 《南昌音系》的严式记音规则。 */
public final class Dict_NanchangYinxiYanshiBiaoyin extends LACDictionary
{
    public static final Dict_NanchangYinxiYanshiBiaoyin INSTANCE = new Dict_NanchangYinxiYanshiBiaoyin();

    private Dict_NanchangYinxiYanshiBiaoyin() {}

    @Override
    public DictCode code() { return DictCode.NCPHON_Y; }

    @Override
    protected Maybe<String> transcribe(LACPinyin pinyin, IPASyllStyle syllStyle, IPAToneStyle toneStyle)
    {
        Maybe<String> raw = pinyin.yinjie.fold(d -> value(handleDandu(d)), this::syllable);
        if (raw.isEmpty()) return Maybe.nothing();
        String syllable = IPAFormatter.formatSyllable(raw.getValue(), syllStyle);
        return pinyin.yindiao.fold(t -> applyTone(syllable, t, toneStyle), ignored -> Maybe.nothing());
    }

    private Maybe<String> syllable(LACPinyin.YinJie syllable)
    {
        String yun = handleYun(syllable);
        return yun == null ? Maybe.nothing() : Maybe.exist(handleShengMu(syllable.getShengmu()) + yun);
    }

    @Override
    protected String handleDandu(LACPinyin.DanDu dandu)
    {
        return switch (dandu)
        {
            case m -> super.handleDandu(dandu);
            case n -> null;
            case ŋ -> "ŋ̩";
        };
    }

    @Override
    protected String handleShengMu(LACPinyin.ShengMu initial)
    {
        return switch (initial)
        {
            case m -> "mᵇ";
            case f -> "ɸ";
            case ŋ -> "ŋᵍ";
            default -> super.handleShengMu(initial);
        };
    }

    /** 严式记音中的介母会受主元音和韵尾影响，所以三个部分一起处理。 */
    protected String handleYun(LACPinyin.YinJie yinjie)
    {
        return switch (yinjie.getJiemu())
        {
            case $ -> handleOpenFinal(yinjie.getYunmu(), yinjie.getYunwei());
            case i -> handleIFinal(yinjie.getYunmu(), yinjie.getYunwei());
            case u -> handleUFinal(yinjie.getYunmu(), yinjie.getYunwei());
            case ü -> handleYuFinal(yinjie.getYunmu(), yinjie.getYunwei());
        };
    }

    private String handleOpenFinal(LACPinyin.YunMu m, LACPinyin.YunWei w)
    {
        return switch (m)
        {
            case ı -> w == LACPinyin.YunWei.$ ? "ï" : null;
            case a -> switch (w) {
                case $ -> "ᴀ"; case i -> "aɪ"; case u -> "ɑo"; case n -> "æn";
                case ŋ -> "ᴀŋ"; case t -> "ᴀt"; case k -> "aʔ"; default -> null;
            };
            case o -> switch (w) {
                case $ -> "o"; case n -> "on"; case ŋ -> "ɔːŋ";
                case t -> "œt"; case k -> "oʔ"; default -> null;
            };
            case e -> switch (w) {
                case u -> "εʊ"; case n -> "en"; case t -> "εt"; case k -> "εʔ"; default -> null;
            };
            case ẹ -> switch (w) {
                case u -> "ĕʊ"; case n -> "ən"; case t -> "ət"; default -> null;
            };
            case ọ -> w == LACPinyin.YunWei.$ ? "ø" : null;
            default -> null;
        };
    }

    private String handleIFinal(LACPinyin.YunMu m, LACPinyin.YunWei w)
    {
        return switch (m)
        {
            case $ -> switch (w) {
                case $ -> "i"; case u -> "ɪu"; case n -> "ɪn"; case t -> "ɪt"; default -> null;
            };
            case a -> switch (w) {
                case $ -> "ɪᴀ"; case ŋ -> "ɪᴀŋ"; case k -> "ɪaʔ"; default -> null;
            };
            case o -> switch (w) {
                case ŋ -> "ɪɔːŋ"; case k -> "ɪoʔ"; default -> null;
            };
            case e -> switch (w) {
                case $ -> "ɪᴇ"; case u -> "ɪeʊ"; case n -> "ien"; case t -> "ɪᴇ"; default -> null;
            };
            case u -> switch (w) {
                case ŋ -> "yʊŋ"; case k -> "yuʔ"; default -> null;
            };
            default -> null;
        };
    }

    private String handleUFinal(LACPinyin.YunMu m, LACPinyin.YunWei w)
    {
        return switch (m)
        {
            case $ -> switch (w) {
                case $ -> "u"; case i -> "uɪ"; case n -> "uən"; case ŋ -> "ʊŋ";
                case t -> "ʊət"; case k -> "uʔ"; case l -> "ʊəl"; default -> null;
            };
            case a -> switch (w) {
                case $ -> "ʊɑ"; case i -> "ʊaɪ"; case n -> "ʊæn";
                case ŋ -> "ʊᴀŋ"; case t -> "ʊat"; default -> null;
            };
            case o -> switch (w) {
                case $ -> "ʊo"; case n -> "ʊon"; case k -> "ʊoʔ"; default -> null;
            };
            case e -> w == LACPinyin.YunWei.t ? "ʊεt" : null;
            default -> null;
        };
    }

    private String handleYuFinal(LACPinyin.YunMu m, LACPinyin.YunWei w)
    {
        return switch (m)
        {
            case $ -> switch (w) {
                case $ -> "y"; case n -> "yɪn"; case t -> "yt"; default -> null;
            };
            case a -> w == LACPinyin.YunWei.$ ? "yᴀ" : null;
            case e -> switch (w) {
                case n -> "yen"; case t -> "yᴇt"; default -> null;
            };
            default -> null;
        };
    }

    private Maybe<String> applyTone(String syllable, LACPinyin.YinDiao tone, IPAToneStyle style)
    {
        if (tone == LACPinyin.YinDiao.$) return Maybe.exist(syllable);
        if (tone == LACPinyin.YinDiao.t7) return Maybe.nothing();
        if (style == IPAToneStyle.FOUR_CORNER || tone == LACPinyin.YinDiao.t0 ||
                tone == LACPinyin.YinDiao.t2 || tone == LACPinyin.YinDiao.t4 ||
                tone == LACPinyin.YinDiao.t5)
            return Dict_NanchangFangyanCidian.INSTANCE.applyTone(syllable, tone, style);

        String value = switch (style)
        {
            case FIVE_DEGREE_LINE -> switch (tone) {
                case t1 -> syllable + "_˧˩"; case t3 -> syllable + "_˧˩˧";
                case t6 -> syllable + "_˥˥˥"; default -> throw new IllegalStateException("已由标准声调规则处理");
            };
            case FIVE_DEGREE_NUM -> switch (tone) {
                case t1 -> syllable + "³¹"; case t3 -> syllable + "³¹³";
                case t6 -> syllable + "⁵⁵"; default -> throw new IllegalStateException("已由标准声调规则处理");
            };
            case FOUR_CORNER -> throw new IllegalStateException("已由标准声调规则处理");
        };
        return Maybe.exist(value);
    }

    private Maybe<String> value(String value)
    {
        return value == null ? Maybe.nothing() : Maybe.exist(value);
    }
}
