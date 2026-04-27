package com.shuowen.yuzong.Linguistics.Scheme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import lombok.Data;

/**
 * {@code Splitted Pinyin}，分割了的拼音。音节和音调拆开，容易做处理。类型安全
 */
@Data
public class SPinyin
{
    private final String syll;
    private final Maybe<String> tone;

    private SPinyin(String syll, Maybe<String> tone)
    {
        this.syll = syll;
        this.tone = tone;
    }

    public static SPinyin valueOf(String s)
    {
        return SPinyin.of(s);
    }

    @JsonCreator
    public static SPinyin of(String text)
    {
        if (!StringTool.isTrimValid(text))
            throw new InvalidPinyinException("缺少拼音");
        if (text.contains(" ")) throw new InvalidPinyinException(
                String.format("%s拼音里不能包含空格", text)
        );

        var tmp = trySplit(text);
        return new SPinyin(tmp.getLeft(), tmp.getRight());
    }

    public static SPinyin of(String syll, Maybe<String> tone)
    {
        return new SPinyin(syll, tone);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return syll + (tone.isValid() ? tone.getValue() : "");
    }

    /**
     * 尝试将一个字符串拆成声母和声调，如果没有音调补0，所有拼音都可以通用
     */
    private static Pair<String, Maybe<String>> trySplit(String text)
    {
        StringTool.checkTrimValid(text); // 如果是空的，取最后一个会报错

        String[] parts = text.split("(?=\\d)", 2);

        if (parts.length == 1) return Pair.of(text, Maybe.nothing());
        else return Pair.of(parts[0], Maybe.exist(parts[1]));
    }
}
