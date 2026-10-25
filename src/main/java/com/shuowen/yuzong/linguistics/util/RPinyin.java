package com.shuowen.yuzong.linguistics.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;

import java.text.Normalizer;

/**
 * {@code Read Pinyin}，作为结果展示的拼音的载体，本质是字符串，但是
 * <br> 1. 类型安全
 * <br> 2. 只读，稳定
 */
@EqualsAndHashCode
public class RPinyin implements RichTextPinyin
{
    private final String pinyin;

    private RPinyin(String pinyin)
    {
        this.pinyin = pinyin;
    }

    @JsonCreator
    public static RPinyin of(String pinyin)
    {
        return new RPinyin(pinyin);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return toShowString(); // 常见的用途在展示
    }

    @Override
    public String toOriginalString()
    {
        return pinyin;
    }

    @Override
    public String toHandleString()
    {
        return Normalizer.normalize(pinyin, Normalizer.Form.NFC);
    }

    @Override
    public String toShowString()
    {
        return String.format(" [%s] ",toHandleString());
    }
}
