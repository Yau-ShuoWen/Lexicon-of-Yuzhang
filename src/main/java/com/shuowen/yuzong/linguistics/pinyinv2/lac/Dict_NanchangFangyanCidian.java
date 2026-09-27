package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 《南昌方言词典》的记音规则。
 */
public final class Dict_NanchangFangyanCidian extends LACZuheCidian
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

    @Override
    protected String handleYun(LACPinyin.YinJie yinjie)
    {
        return handleJieMu(yinjie.getJiemu()) +
                handleYunMu(yinjie.getYunmu(), yinjie.getYunwei()) +
                handleYunWei(yinjie.getYunwei());
    }

    /**
     * 收录范围和音值计算分开：这里只回答本辞书有没有这个韵母。
     */
    @Override
    protected boolean supports(LACPinyin.YinJie syllable)
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

    @Override
    protected Maybe<String> toneValue(LACPinyin.YinDiao tone)
    {
        return Maybe.exist(switch (tone)
        {
            case t0 -> "0";
            case t1 -> "42";
            case t2 -> "24";
            case t3 -> "213";
            case t4 -> "35";
            case t5 -> "11";
            case t6 -> "5";
            case t7 -> "2";
            case $ -> throw new IllegalStateException("无声调不需要调值");
        });
    }
}
