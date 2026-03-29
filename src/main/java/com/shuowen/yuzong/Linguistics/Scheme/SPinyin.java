package com.shuowen.yuzong.Linguistics.Scheme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import lombok.Data;

/**
 * {@code Splitted Pinyin}，分割了的拼音。音节和音调拆开，容易做处理。类型安全
 */
@Data
public class SPinyin
{
    private String syllable;
    private Integer tone;

    private SPinyin(String syllable, Integer tone)
    {
        this.syllable = syllable;
        this.tone = tone;
    }

    private SPinyin(UniPinyin<?> py)
    {
        syllable = py.getPinyin();
        tone = py.getTone();
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
        return syllable + tone;
    }

    /**
     * 尝试将一个字符串拆成声母和声调，如果没有音调补0，所有拼音都可以通用
     */
    //TODO 南宁话：谁说汉语音调小于十个的？回答我！
    public static Pair<String, Integer> trySplit(String text)
    {
        StringTool.checkTrimValid(text); // 如果是空的，取最后一个会报错

        char ch = StringTool.back(text);
        return NumberTool.closeBetween(ch, '0', '9') ?
                Pair.of(StringTool.deleteBack(text), ch - '0') :
                Pair.of(text, 0);
    }
}
