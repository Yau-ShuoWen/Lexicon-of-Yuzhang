package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/** 《南昌方言研究》的记音规则。 */
public final class Dict_NanchangFangyanYanjiu extends LACZuheCidian
{
    public static final Dict_NanchangFangyanYanjiu INSTANCE = new Dict_NanchangFangyanYanjiu();
    private Dict_NanchangFangyanYanjiu() {}
    @Override public DictCode code() { return DictCode.NCSTUDY; }

    @Override protected boolean supports(LACPinyin.YinJie y)
    {
        return Dict_NanchangFangyanCidian.INSTANCE.supports(y);
    }

    @Override protected String handleYun(LACPinyin.YinJie y)
    {
        return handleJieMu(y.getJiemu()) + handleYunMu(y.getYunmu(), y.getYunwei()) + handleYunWei(y.getYunwei());
    }

    private String handleYunMu(LACPinyin.YunMu m, LACPinyin.YunWei w)
    {
        if (m == LACPinyin.YunMu.e) return "e";
        return Dict_NanchangFangyanCidian.INSTANCE.handleYunMu(m, w);
    }

    @Override protected Maybe<String> toneValue(LACPinyin.YinDiao tone)
    {
        return Maybe.exist(switch (tone)
        {
            case t0 -> "0"; case t1 -> "42"; case t2 -> "24"; case t3 -> "213";
            case t4 -> "44"; case t5 -> "21"; case t6 -> "5"; case t7 -> "1";
            case $ -> throw new IllegalStateException();
        });
    }
}
