package com.shuowen.yuzong.data.domain.Reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

@Data
public class DictCode
{
    private final String code;
    private final boolean strict;

    @JsonCreator
    public DictCode(String text)
    {
        String[] parts = text.split("_");
        code = parts[0];
        strict = parts.length > 1;
    }

    public DictCode(String code, boolean strict)
    {
        this.code = code;
        this.strict = strict;
    }

    @Override
    @JsonValue
    public String toString()
    {
        return code;
    }
}
