package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.util.tuple.Maybe;

/** 适用于“声母 + 韵部 + 声调”流程的辞书骨架，不规定韵部内部怎样组合。 */
abstract class LACZuheCidian extends LACDictionary
{
    @Override
    protected Maybe<String> transcribe(LACPinyin pinyin, IPASyllStyle syllStyle, IPAToneStyle toneStyle)
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
                            ? Maybe.exist(syllable)
                            : toneValue(tone).handleIfExist(value -> formatTone(syllable, tone, toneStyle, value)),
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
