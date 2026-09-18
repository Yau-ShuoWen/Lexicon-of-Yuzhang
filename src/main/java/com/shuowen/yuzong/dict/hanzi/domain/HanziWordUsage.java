package com.shuowen.yuzong.dict.hanzi.domain;

import com.shuowen.yuzong.dict.data.domain.Word.CiyuItem;
import com.shuowen.yuzong.linguistics.pinyin.UniPinyin;
import com.shuowen.yuzong.linguistics.util.RPinyins;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.text.UString;
import lombok.Getter;

/** 词语中某个字位实际使用的方言读音。 */
@Getter
public class HanziWordUsage
{
    private final Integer wordId;
    private final UString word;
    private final RPinyins pinyin;
    private final UniPinyin characterPinyin;

    public HanziWordUsage(CiyuItem item, int characterIndex, Dialect dialect)
    {
        wordId = item.getId();
        word = item.getCiyu();
        pinyin = item.getPinyin(dialect);
        characterPinyin = dialect.trustedCreatePinyin(item.getMainPy().get(characterIndex));
    }
}
