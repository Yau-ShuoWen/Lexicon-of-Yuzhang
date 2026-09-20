package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/** 《南昌音系》的宽式记音规则。 */
public final class Dict_NanchangYinxi extends LACZuheCidian
{
    public static final Dict_NanchangYinxi INSTANCE = new Dict_NanchangYinxi();
    private Dict_NanchangYinxi() {}
    @Override public DictCode code() { return DictCode.NCPHON; }

    @Override protected String handleDandu(LACPinyin.DanDu d)
    {
        return switch (d) { case m -> super.handleDandu(d); case n -> null; case ŋ -> "ŋ̩"; };
    }

    @Override protected boolean supports(LACPinyin.YinJie y)
    {
        return switch (y.getYun())
        {
            case "üa", "üen", "üet", "ek", "ul" -> true;
            case "e", "ue", "üe", "oi", "ei", "ẹi", "üon", "uoŋ", "uot", "üot", "uak" -> false;
            default -> Dict_NanchangFangyanCidian.INSTANCE.supports(y);
        };
    }

    @Override protected String handleYun(LACPinyin.YinJie y)
    {
        return switch (y.getYun())
        {
            case "ọ" -> "ø"; case "üa" -> "ya"; case "ı" -> "ï";
            case "ẹu" -> "eu"; case "ieu" -> "ieu"; case "en" -> "en";
            case "ẹn" -> "ən"; case "ien" -> "ien"; case "un" -> "uən";
            case "ün" -> "yin"; case "üen" -> "yen"; case "oŋ" -> "oŋ";
            case "ioŋ" -> "ioŋ"; case "iuŋ" -> "yuŋ"; case "ot" -> "œt";
            case "ẹt" -> "ət"; case "ut" -> "uət"; case "üet" -> "yεt";
            case "ok" -> "oʔ"; case "ek" -> "εʔ"; case "iok" -> "ioʔ";
            case "iuk" -> "yuʔ"; case "uok" -> "uoʔ"; case "ul" -> "uəl";
            default -> Dict_NanchangFangyanCidian.INSTANCE.handleYun(y);
        };
    }

    @Override protected Maybe<String> toneValue(LACPinyin.YinDiao tone)
    {
        if (tone == LACPinyin.YinDiao.t7) return Maybe.nothing();
        return Maybe.exist(switch (tone)
        {
            case t0 -> "0"; case t1 -> "31"; case t2 -> "24"; case t3 -> "313";
            case t4 -> "35"; case t5 -> "11"; case t6 -> "55";
            case $, t7 -> throw new IllegalStateException();
        });
    }
}
