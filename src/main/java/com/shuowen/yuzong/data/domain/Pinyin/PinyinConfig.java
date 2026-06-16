package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.IPA.IPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.DictCodeExt;
import com.shuowen.yuzong.data.domain.Reference.DictGroup;
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

    public Maybe<String> searchIPA(IPinyin pinyin, DictCode dict)
    {
        return IPACache.get(pinyin,dict,this);
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
