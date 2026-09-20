package com.shuowen.yuzong.dict.pinyin.data;

import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.PinyinMode;
import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.dict.data.domain.Reference.DictCodeExt;
import com.shuowen.yuzong.dict.data.domain.Reference.DictGroup;
import com.shuowen.yuzong.linguistics.pinyinv2.UniPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.tuple.Maybe;
import lombok.Getter;

@Getter
public class PinyinConfig
{
    private final Language language;
    private final Dialect dialect;
    private final PinyinMode pinyinMode;
    private final IPASyllStyle syllStyle;
    private final IPAToneStyle toneStyle;
    private final DictGroup dictGroup;

    public PinyinConfig(Language l, Dialect d, PinyinMode m, IPASyllStyle s, IPAToneStyle t)
    {
        language = l;
        dialect = d;
        pinyinMode = m;
        syllStyle = s;
        toneStyle = t;
        dictGroup = DictGroup.of(d);
    }

    public PinyinConfig(Language l, Dialect d)
    {
        language = l;
        dialect = d;
        pinyinMode = PinyinMode.PROFESSIONAL;
        syllStyle = IPASyllStyle.CHINESE_SPECIAL;
        toneStyle = IPAToneStyle.FIVE_DEGREE_LINE;
        dictGroup = DictGroup.of(d);
    }

    public Maybe<String> searchIPA(UniPinyin pinyin, DictCode dict)
    {
        try
        {
            var newDict = com.shuowen.yuzong.linguistics.pinyinv2.DictCode.of(dict.toString());
            return pinyin.searchIPA(newDict, syllStyle, toneStyle)
                    .handleIfExist(ipa -> String.format("[%s]", ipa));
        }
        catch (IllegalArgumentException ignored)
        {
            return Maybe.nothing();
        }
    }

    public String getDictName(DictCode dict)
    {
        return dictGroup.containDict(dict) ?
                dictGroup.getName(dict, language) :
                "找不到对应字典。dictionary not found.";
    }

    public String getDictName(DictCodeExt dict)
    {
        return dictGroup.containDict(dict.getCode()) ?
                dictGroup.getName(dict, language) :
                "找不到对应字典。dictionary not found.";
    }
}
