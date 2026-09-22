package com.shuowen.yuzong.linguistics.pinyinv2.lac;

import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.linguistics.pinyinv2.DictionaryRegistry;
import com.shuowen.yuzong.linguistics.pinyinv2.PronunciationDictionary;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.tuple.Maybe;

/**
 * 南昌话拼音到各辞书记音规则的入口。
 */
public final class LACIPA
{
    private LACIPA()
    {
    }

    public static String handle(LACPinyin pinyin)
    {
        return handle(DictCode.NCDICT, pinyin);
    }

    public static String handle(DictCode dict, LACPinyin pinyin)
    {
        Maybe<String> result = query(
                pinyin, dict, IPASyllStyle.CHINESE_SPECIAL, IPAToneStyle.FIVE_DEGREE_LINE
        );
        if (result.isEmpty())
            throw new InvalidPinyinException(dict + "没有记录南昌话拼音：" + pinyin);
        return result.getValue();
    }

    public static Maybe<String> query(LACPinyin pinyin)
    {
        return query(
                pinyin, DictCode.NCDICT,
                IPASyllStyle.CHINESE_SPECIAL, IPAToneStyle.FIVE_DEGREE_LINE
        );
    }

    public static Maybe<String> query(LACPinyin pinyin, DictCode dict)
    {
        return query(
                pinyin, dict,
                IPASyllStyle.CHINESE_SPECIAL, IPAToneStyle.FIVE_DEGREE_LINE
        );
    }

    public static Maybe<String> query(
            LACPinyin pinyin,
            DictCode dict,
            IPASyllStyle syllStyle,
            IPAToneStyle toneStyle
    )
    {
        Maybe<PronunciationDictionary> dictionary = DictionaryRegistry.findIpaDictionary(dict);
        return dictionary.isValid()
                ? dictionary.getValue().transcribe(pinyin, syllStyle, toneStyle)
                : Maybe.nothing();
    }
}
