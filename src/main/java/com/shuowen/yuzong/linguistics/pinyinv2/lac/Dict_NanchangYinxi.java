package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 《南昌音系》的宽式记音规则。
 * <br>
 * 疯狂，但是我们不能苛责一个古人
 */
public final class Dict_NanchangYinxi extends LACNormalDict
{
    public static final Dict_NanchangYinxi INSTANCE = new Dict_NanchangYinxi();

    private static final YunSupport SUPPORT = Dict_NanchangFangyanCidian.SUPPORT
            .add("üa").remove("e", "ue", "üe")       // a ia ua üa ie
            .remove("oi", "ei")                // ai uai ui
            .add("üen", "üet").remove("üon", "üot")  // 认为 üe 更贴近
            .remove("uoŋ", "uot", "uak", "ek")
            .add("ul");//神人

    private Dict_NanchangYinxi()
    {
    }

    @Override
    public DictCode code()
    {
        return DictCode.NCPHON;
    }

    @Override
    protected String handleDandu(LACPinyin.DanDu d)
    {
        return switch (d)
        {
            case m -> "m̩";
            case n -> null;
            case ŋ -> "ŋ̩";
        };
    }

    @Override
    protected boolean supports(LACPinyin.YinJie y)
    {
        return SUPPORT.contains(y);
    }

    @Override
    protected String handleShengMu(LACPinyin.ShengMu initial)
    {
        return switch (initial)
        {
            case f -> "ɸ";
            default -> super.handleShengMu(initial);
        };
    }

    @Override
    protected String handleYun(LACPinyin.YinJie y)
    {
        return switch (y.getYun())
        {
            // 特殊内容
            case "ı" -> "ï";
            case "ọ" -> "ø";

            // 无韵尾内容：允许一个新的韵尾
            case "üa" -> "ya";

            // 元音韵尾
            case "eu" -> "eu";
            case "ieu" -> "ieu";
            case "ẹu" -> "eu";
            case "ẹi" -> "ui";

            case "en" -> "en";
            case "ẹn" -> "ən";
            case "ien" -> "ien";
            case "un" -> "uən";
            case "ün" -> "yin";
            case "üen" -> "yen";
            case "oŋ" -> "oŋ";
            case "ioŋ" -> "ioŋ";
            case "iuŋ" -> "yuŋ";
            case "ot" -> "œt";
            case "ẹt" -> "ət";
            case "ut" -> "uət";
            case "üet" -> "yεt";
            case "ok" -> "oʔ";
            case "ek" -> "εʔ";
            case "iok" -> "ioʔ";
            case "iuk" -> "yuʔ";
            case "uok" -> "uoʔ";
            case "ul" -> "uəl";
            default -> Dict_NanchangFangyanCidian.INSTANCE.handleYun(y);
        };
    }

    // 六七声是一个入声
    @Override
    protected Maybe<String> toneValue(LACPinyin.YinDiao tone)
    {
        return Maybe.exist(switch (tone)
        {
            case t0 -> "0";
            case t1 -> "31";
            case t2 -> "24";
            case t3 -> "313";
            case t4 -> "35";
            case t5 -> "11";
            case t6 -> "55";
            case t7 -> throw new IllegalArgumentException("《南昌音系》未收录第七调");
            case $ -> throw new IllegalStateException("无声调的已经在之前处理过，此处为不应到达的位置");
        });
    }
}
