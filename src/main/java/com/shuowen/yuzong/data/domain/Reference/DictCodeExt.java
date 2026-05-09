package com.shuowen.yuzong.data.domain.Reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

@Data
public class DictCodeExt
{
    private final DictCode code;
    private final boolean strict;

    @JsonCreator
    public DictCodeExt(String text)
    {
        String[] parts = text.split("_");
        code = new DictCode(parts[0]);
        strict = parts.length > 1;
    }

    public static DictCodeExt of(String text)
    {
        return new DictCodeExt(text);
    }

    @Override
    @JsonValue
    public String toString()
    {
        return code.getCode();
    }
}
