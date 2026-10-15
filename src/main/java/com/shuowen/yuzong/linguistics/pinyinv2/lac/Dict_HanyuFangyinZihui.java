package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 《汉语方音字汇（第二版）》的记音规则。
 * <br>
 * 拼音风格：大事化小，小事化了，使用最通用的IPA解决问题
 */
public final class Dict_HanyuFangyinZihui extends LACZuheCidian
{
    public static final Dict_HanyuFangyinZihui INSTANCE = new Dict_HanyuFangyinZihui();

    private static final YunSupport SUPPORT = Dict_NanchangFangyanCidian.SUPPORT.remove("oi", "ei");

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
        return SUPPORT.contains(y);
    }

    @Override
    protected String handleYun(LACPinyin.YinJie y)
    {
        return handleJieMu(y.getJiemu()) + handleYunMu(y.getYunmu()) + handleYunWei(y.getYunwei());
    }

    // e=ε ɨ=ə ɵ=ə
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

    // 韵尾除了喉塞音写作k，其他照旧，读音偏硬
    @Override
    protected String handleYunWei(LACPinyin.YunWei w)
    {
        return w == LACPinyin.YunWei.k ?
                "k" : super.handleYunWei(w);
    }

    // 第五声和第七声依靠韵尾区分
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
            case t5, t7 -> "21";
            case t6 -> "5";
            case $ -> throw new IllegalStateException();
        });
    }
}
