package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 《南昌话音档》的记音规则。
 * <br>
 * 拼音风格：为了压缩韵尾不择手段，非常复杂
 */
public final class Dict_NanchanghuaYindang extends LACNormalDict
{
    public static final Dict_NanchanghuaYindang INSTANCE = new Dict_NanchanghuaYindang();

    private static final YunSupport SUPPORT = Dict_NanchangFangyanCidian.SUPPORT.remove("oi", "ei");

    private Dict_NanchanghuaYindang()
    {
    }

    @Override
    public DictCode code()
    {
        return DictCode.NCRECORD;
    }

    @Override
    protected boolean supports(LACPinyin.YinJie y)
    {
        return SUPPORT.contains(y);
    }

    // 原文后记写，合并了韵尾，用元音区别代替
    // at/ak  ɑʔ/aʔ
    // ot/ok  ɵʔ/ɔʔ
    // ut/uk  uɨʔ/uʔ 所以这里是最复杂的一个韵母
    @Override
    protected String handleYun(LACPinyin.YinJie y)
    {
        if (y.getYun().equals("ut")) return "uɨʔ";

        return handleJieMu(y.getJiemu()) +
                handleYunMu(y.getYunmu(), y.getYunwei()) +
                handleYunWei(y.getYunwei());
    }

    // 原文后记写，合并了韵尾，用元音区别代替（a o u）
    private String handleYunMu(LACPinyin.YunMu m, LACPinyin.YunWei w)
    {
        return switch (m)
        {
            case $ -> "";
            case ı -> "ɿ";
            case a -> switch (w)
            {
                case $, u, ŋ, k -> "ɑ";
                case i, n, t -> "a";
                case l -> null;
            };
            /*
              a 只受到韵尾的影响，作者的小巧思
                  a  ai  au  an  ang  at  ak
              -------------------------------
               ɑ  ✓      ✓       ✓        ✓
               a     ✓        ✓        ✓

             */

            case o -> switch (w)
            {
                case n, t -> "ɵ";  // 舌尖组，yzhy喜欢的严式标音
                case ŋ, k -> "ɔ";  // 舌根组
                default -> "o";
            };
            /*
              o 只受到韵尾的影响，作者的小巧思
                  o  on  ong  ot  ok
              -----------------------
               o  ✓
               ɔ     ✓        ✓
               ɵ          ✓        ✓

             */

            case e -> switch (w)
            {
                case u, n, t -> "ε";
                default -> "e";
            };

            case ẹ -> "ɨ";
            case ọ -> "ɵ";
            case u -> "u";
        };
    }

    // 原文后记写，合并了韵尾，用元音区别代替
    @Override
    protected String handleYunWei(LACPinyin.YunWei w)
    {
        return w == LACPinyin.YunWei.t ?
                "ʔ" : super.handleYunWei(w);
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
            case t6 -> "5"; // 原文P10写作 ʔ5 但是ʔ由韵尾提供
            case t7 -> "2"; //            ʔ2
            case $ -> throw new IllegalStateException("无声调的已经在之前处理过，此处为不应到达的位置");
        });
    }
}
