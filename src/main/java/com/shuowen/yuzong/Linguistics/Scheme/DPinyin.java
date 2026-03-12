package com.shuowen.yuzong.Linguistics.Scheme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import lombok.Data;

import static com.shuowen.yuzong.data.domain.Pinyin.PinyinFormatter.trySplit;

/**
 * 用作存储拼音的对象
 */
@Data
public class DPinyin
{
    /**
     * 读状态展示的是show
     * 写状态用的是syllabe
     */
    private enum Statue
    {READ, HANDLE}

    private String syllable;
    private Maybe<Integer> tone;
    private String show;
    private final Statue statue;

    private DPinyin(String syllable, Integer tone, String show, Statue statue)
    {
        this.syllable = syllable;
        this.tone = Maybe.uncertain(tone);
        this.show = show;
        this.statue = statue;
    }

    public static DPinyin valueOf(String s)
    {
        return DPinyin.handle(s);
    }

    /**
     * 前端给后端，写模式，
     */
    @JsonCreator
    public static DPinyin handle(String text)
    {
        StringTool.checkTrimValid(text);

        var tmp = trySplit(text);
        return new DPinyin(tmp.getLeft(), tmp.getRight(), "未知格式", Statue.HANDLE);
    }

    /**
     * 后端给前端，读模式
     */
    public static DPinyin read(UniPinyin<?> pinyin, String text)
    {
        return new DPinyin(pinyin.getPinyin(), pinyin.getTone(), text, Statue.READ);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return switch (statue)
        {
            case READ -> " [" + show + "] ";
            case HANDLE -> syllable + tone.getValueOrDefault(0);
        };
    }
}
