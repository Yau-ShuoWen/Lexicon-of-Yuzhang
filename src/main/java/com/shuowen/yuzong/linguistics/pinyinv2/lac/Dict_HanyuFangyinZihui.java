package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 《汉语方音字汇（第二版）》的记音规则。
 */
public final class Dict_HanyuFangyinZihui extends LACZuheCidian
{
    public static final Dict_HanyuFangyinZihui INSTANCE = new Dict_HanyuFangyinZihui();

    private Dict_HanyuFangyinZihui()
    {
    }

    @Override
    public DictCode code()
    {
        return DictCode.CNDIALDICT;
    }

    @Override
    protected boolean supports(LACPinyin.YinJie y)
    {
        return Dict_NanchangFangyanCidian.INSTANCE.supports(y) && !"oi".equals(y.getYun()) && !"ei".equals(y.getYun());
    }

    @Override
    protected String handleYun(LACPinyin.YinJie y)
    {
        return handleJieMu(y.getJiemu()) + handleYunMu(y.getYunmu()) + handleYunWei(y.getYunwei());
    }

    private String handleYunMu(LACPinyin.YunMu m)
    {
        return switch (m)
        {
            case $ -> "";
            case ı -> "ɿ";
            case a -> "a";
            case o -> "ɔ";
            case e -> "ε";
            case ẹ, ọ -> "ə";
            case u -> "u";
        };
    }

    @Override
    protected String handleYunWei(LACPinyin.YunWei w)
    {
        return w == LACPinyin.YunWei.k ? "k" : super.handleYunWei(w);
    }

    @Override
    protected Maybe<String> toneValue(LACPinyin.YinDiao tone)
    {
        return Maybe.exist(switch (tone)
        {
            case t0 -> "0";
            case t1 -> "42";
            case t2 -> "24";
            case t3 -> "213";
            case t4 -> "45";
            case t5 -> "21";
            case t6 -> "5";
            case t7 -> "21";
            case $ -> throw new IllegalStateException();
        });
    }
}
