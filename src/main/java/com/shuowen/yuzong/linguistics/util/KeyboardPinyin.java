package com.shuowen.yuzong.linguistics.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.text.StringTool;
import com.shuowen.yuzong.util.tuple.Maybe;
import lombok.Data;

import static com.shuowen.yuzong.linguistics.util.SplitedPinyin.trySplit;

@Data
public class KeyboardPinyin
{
    private final String syll;
    private final Maybe<Integer> tone;

    private KeyboardPinyin(String syll, Maybe<Integer> tone)
    {
        this.syll = syll;
        this.tone = tone;
    }

    public static KeyboardPinyin valueOf(String s)
    {
        return KeyboardPinyin.of(s);
    }

    @JsonCreator
    public static KeyboardPinyin of(String text)
    {
        if (!StringTool.isTrimValid(text))
            throw new InvalidPinyinException("缺少拼音");
        if (text.contains(" ")) throw new InvalidPinyinException(
                String.format("%s拼音里不能包含空格", text)
        );

        var tmp = trySplit(text);
        return new KeyboardPinyin(tmp.getLeft(), tmp.getRight());
    }

    public static KeyboardPinyin of(String syll, Maybe<Integer> tone)
    {
        return new KeyboardPinyin(syll, tone);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return syll + (tone.isValid() ? tone.getValue() : "");
    }
}
