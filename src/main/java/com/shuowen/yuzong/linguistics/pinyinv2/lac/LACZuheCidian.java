package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.dict.data.domain.IPA.IPA;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.util.tuple.Maybe;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** 适用于“声母 + 韵部 + 声调”流程的辞书骨架，不规定韵部内部怎样组合。 */
abstract class LACZuheCidian extends LACDictionary
{
    /**
     * 辞书收录的韵部集合。支持以另一部辞书为基础增删韵部，避免在
     * {@link #supports(LACPinyin.YinJie)} 里反复拼接与、或条件。
     */
    protected static final class YunSupport
    {
        private final Set<String> yun;

        private YunSupport(Set<String> yun)
        {
            this.yun = Set.copyOf(yun);
        }

        protected static YunSupport of(String... yun)
        {
            return new YunSupport(Set.of(yun));
        }

        protected YunSupport add(String... yun)
        {
            Set<String> result = new HashSet<>(this.yun);
            result.addAll(Arrays.asList(yun));
            return new YunSupport(result);
        }

        protected YunSupport remove(String... yun)
        {
            Set<String> result = new HashSet<>(this.yun);
            result.removeAll(Arrays.asList(yun));
            return new YunSupport(result);
        }

        protected boolean contains(LACPinyin.YinJie yinjie)
        {
            return yun.contains(yinjie.getYun());
        }
    }

    @Override
    protected Maybe<IPA> transcribe(LACPinyin pinyin, IPASyllStyle syllStyle, IPAToneStyle toneStyle)
    {
        try
        {
            Maybe<String> raw = pinyin.yinjieOrDandu.fold(
                    dandu -> Maybe.uncertain(handleDandu(dandu)),
                    yinjie -> supports(yinjie)
                            ? Maybe.exist(handleShengMu(yinjie.getShengmu()) + handleYun(yinjie))
                            : Maybe.nothing()
            );
            if (raw.isEmpty()) return Maybe.nothing();
            String syllable = IPAFormatter.formatSyllable(raw.getValue(), syllStyle);
            return pinyin.yindiao.fold(
                    tone -> tone == LACPinyin.YinDiao.$
                            ? Maybe.exist(new IPA(syllable))
                            : toneValue(tone).handleIfExist(value -> new IPA(formatTone(syllable, tone, toneStyle, value))),
                    ignored -> Maybe.nothing()
            );
        } catch (Exception ignored)
        {
            // 辞书规则无法解释该拼音时，视为该辞书没有有效 IPA。
            return Maybe.nothing();
        }
    }

    protected abstract boolean supports(LACPinyin.YinJie yinjie);

    protected abstract String handleYun(LACPinyin.YinJie yinjie);

    protected abstract Maybe<String> toneValue(LACPinyin.YinDiao tone);
}
