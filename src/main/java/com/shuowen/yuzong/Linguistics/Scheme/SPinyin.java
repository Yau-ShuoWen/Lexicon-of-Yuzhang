package com.shuowen.yuzong.Linguistics.Scheme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import lombok.Data;

import static com.shuowen.yuzong.data.domain.Pinyin.PinyinFormatter.trySplit;

/**
 * {@code Splitted Pinyin}，分割了的拼音。音节和音调拆开，容易做处理。类型安全
 */
@Data
public class SPinyin
{
    private String syllable;
    private Maybe<Integer> tone;

    private SPinyin(String syllable, Integer tone)
    {
        this.syllable = syllable;
        this.tone = Maybe.uncertain(tone);
    }

    private SPinyin(UniPinyin<?> py)
    {
        syllable = py.getPinyin();
        tone = Maybe.exist(py.getTone());
    }

    public static SPinyin valueOf(String s)
    {
        return SPinyin.of(s);
    }

    @JsonCreator
    public static SPinyin of(String text)
    {
        StringTool.checkTrimValid(text);

        var tmp = trySplit(text);
        return new SPinyin(tmp.getLeft(), tmp.getRight());
    }

    public static SPinyin of(UniPinyin<?> py)
    {
        return new SPinyin(py);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return syllable + tone.getValueOrDefault(0);
    }
}
