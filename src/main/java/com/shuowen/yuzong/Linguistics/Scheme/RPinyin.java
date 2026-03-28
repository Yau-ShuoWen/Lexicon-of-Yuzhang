package com.shuowen.yuzong.Linguistics.Scheme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.Getter;

/**
 * {@code Read Pinyin}，作为结果展示的拼音的载体，本质是字符串，但是
 * <br> 1. 类型安全
 * <br> 2. 只读，稳定
 */
@Getter
public class RPinyin
{
    private String pinyin;

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
        return String.format(" [%s] ", pinyin);
    }
}
