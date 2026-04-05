package com.shuowen.yuzong.Linguistics.Scheme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import lombok.Data;

import java.util.List;

@Data
public class SPinyins
{
    private final List<SPinyin> pinyin;

    private SPinyins(String text)
    {
        pinyin = ListTool.mapping(List.of(text.trim().split("\\s+")), SPinyin::of);
    }

    private SPinyins(List<SPinyin> pinyin)
    {
        this.pinyin = pinyin;
    }

    @JsonCreator
    public static SPinyins of(String text)
    {
        return new SPinyins(text);
    }

    public static SPinyins of(List<SPinyin> pinyin)
    {
        return new SPinyins(pinyin);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return String.join(" ", ListTool.mapping(pinyin, SPinyin::toString));
    }
}
