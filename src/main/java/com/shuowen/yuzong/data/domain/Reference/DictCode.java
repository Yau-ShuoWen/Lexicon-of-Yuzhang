package com.shuowen.yuzong.data.domain.Reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

@Data
public class DictCode
{
    private final String code;

    @JsonCreator
    public DictCode(String text)
    {
        code = text;
    }

    public static DictCode of(String text)
    {
        return new DictCode(text);
    }

    public static DictCode valueOf(String text)
    {
        return new DictCode(text);
    }

    @Override
    @JsonValue
    public String toString()
    {
        return code;
    }
}
