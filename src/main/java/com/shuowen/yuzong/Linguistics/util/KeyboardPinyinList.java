package com.shuowen.yuzong.Linguistics.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.util.ext.list.ListTool;
import lombok.Data;

import java.util.List;

@Data
public class KeyboardPinyinList
{
    private final List<KeyboardPinyin> pinyin;

    private KeyboardPinyinList(String text)
    {
        pinyin = ListTool.mapping(text.trim().split("\\s+"), KeyboardPinyin::of);
    }

    private KeyboardPinyinList(List<KeyboardPinyin> pinyin)
    {
        this.pinyin = pinyin;
    }

    @JsonCreator
    public static KeyboardPinyinList of(String text)
    {
        return new KeyboardPinyinList(text);
    }

    public static KeyboardPinyinList of(List<KeyboardPinyin> pinyin)
    {
        return new KeyboardPinyinList(pinyin);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return String.join(" ", ListTool.mapping(pinyin, KeyboardPinyin::toString));
    }

    public int size()
    {
        return pinyin.size();
    }
}
