package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/** 《南昌话音档》的记音规则。 */
public final class Dict_NanchanghuaYindang extends LACZuheCidian
{
    public static final Dict_NanchanghuaYindang INSTANCE = new Dict_NanchanghuaYindang();
    private Dict_NanchanghuaYindang() {}
    @Override public DictCode code() { return DictCode.NCRECORD; }

    @Override protected boolean supports(LACPinyin.YinJie y)
    {
        return Dict_NanchangFangyanCidian.INSTANCE.supports(y) && !"oi".equals(y.getYun()) && !"ei".equals(y.getYun());
    }

    @Override protected String handleYun(LACPinyin.YinJie y)
    {
        if (y.getJiemu() == LACPinyin.JieMu.u && y.getYunmu() == LACPinyin.YunMu.$ && y.getYunwei() == LACPinyin.YunWei.t)
            return "uɨʔ";
        return handleJieMu(y.getJiemu()) + handleYunMu(y.getJiemu(), y.getYunmu(), y.getYunwei()) + handleYunWei(y.getYunwei());
    }

    private String handleYunMu(LACPinyin.JieMu j, LACPinyin.YunMu m, LACPinyin.YunWei w)
    {
        return switch (m)
        {
            case $ -> ""; case ı -> "ɿ";
            case a -> w == LACPinyin.YunWei.i || w == LACPinyin.YunWei.n || w == LACPinyin.YunWei.t ||
                    j == LACPinyin.JieMu.u && w != LACPinyin.YunWei.ŋ && w != LACPinyin.YunWei.k ? "a" : "ɑ";
            case o -> switch (w) {
                case n, t -> "ɵ"; case ŋ, k -> "ɔ"; default -> "o";
            };
            case e -> switch (w) {
                case u, n, t -> "ε"; default -> "e";
            };
            case ẹ -> "ɨ"; case ọ -> "ɵ"; case u -> "u";
        };
    }

    @Override protected String handleYunWei(LACPinyin.YunWei w)
    {
        return w == LACPinyin.YunWei.t ? "ʔ" : super.handleYunWei(w);
    }

    @Override protected Maybe<String> toneValue(LACPinyin.YinDiao tone)
    {
        return Maybe.exist(switch (tone)
        {
            case t0 -> "0"; case t1 -> "42"; case t2 -> "24"; case t3 -> "213";
            case t4 -> "45"; case t5 -> "21"; case t6 -> "5"; case t7 -> "2";
            case $ -> throw new IllegalStateException();
        });
    }
}
