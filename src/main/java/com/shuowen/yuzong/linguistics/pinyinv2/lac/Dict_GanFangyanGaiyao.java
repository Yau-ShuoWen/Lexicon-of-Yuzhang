package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/** 《赣方言概要》的记音规则。 */
public final class Dict_GanFangyanGaiyao extends LACZuheCidian
{
    public static final Dict_GanFangyanGaiyao INSTANCE = new Dict_GanFangyanGaiyao();
    private Dict_GanFangyanGaiyao() {}
    @Override public DictCode code() { return DictCode.GANSUM; }

    @Override protected boolean supports(LACPinyin.YinJie y)
    {
        return Dict_NanchangFangyanCidian.INSTANCE.supports(y) && !"oi".equals(y.getYun()) && !"ei".equals(y.getYun());
    }

    @Override protected String handleYun(LACPinyin.YinJie y)
    {
        return handleJieMu(y.getJiemu()) + handleYunMu(y.getYunmu(), y.getYunwei()) + handleYunWei(y.getYunwei());
    }

    private String handleYunMu(LACPinyin.YunMu m, LACPinyin.YunWei w)
    {
        return switch (m)
        {
            case $ -> ""; case ı -> "ɿ"; case a -> "a"; case o -> "o"; case e -> "e";
            case ẹ -> w == LACPinyin.YunWei.i ? "ə" : "ɪ";
            case ọ -> "ə"; case u -> "u";
        };
    }

    @Override protected Maybe<String> toneValue(LACPinyin.YinDiao tone)
    {
        return Maybe.exist(switch (tone)
        {
            case t0 -> "0"; case t1 -> "42"; case t2 -> "24"; case t3 -> "213";
            case t4 -> "45"; case t5 -> "21"; case t6 -> "5"; case t7 -> "1";
            case $ -> throw new IllegalStateException();
        });
    }
}
