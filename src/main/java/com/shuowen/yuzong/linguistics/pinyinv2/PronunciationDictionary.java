package com.shuowen.yuzong.linguistics.pinyinv2;

import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 一部可以把方言拼音解释为国际音标的辞书。
 * <p>
 * 这里只统一调用边界，不规定辞书内部如何拆分、查找或组合音值。
 */
public interface PronunciationDictionary
{
    DictCode code();

    Dialect dialect();

    default ScTcText name()
    {
        return code().getBookname();
    }

    default Language language()
    {
        return code().getLanguage();
    }

    Maybe<String> transcribe(
            UniPinyin pinyin,
            IPASyllStyle syllStyle,
            IPAToneStyle toneStyle
    );
}
