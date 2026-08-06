package com.shuowen.yuzong.linguistics.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class DatabasePinyin
{
    private final String pinyin;

    private DatabasePinyin(String pinyin)
    {
        this.pinyin = pinyin;
    }

    @JsonCreator
    public static DatabasePinyin of(String pinyin)
    {
        return new DatabasePinyin(pinyin);
    }

    @JsonValue
    @Override
    @Deprecated // 获得的时候加上判断，除了框架，不能自己调用
    public String toString()
    {
        return String.format("[%s]", pinyin);
    }

    /**
     * 如果存储的是单个内容，就没有中括号，否则有
     */
    public String toString(boolean single)
    {
        return single ? pinyin : String.format("[%s]", pinyin);
    }
}
