package com.shuowen.yuzong.data.domain.Reference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

@Data
public class Dictionary
{
    private final String code;

    @JsonCreator
    public Dictionary(String code)
    {
        this.code = code;
    }

    @Override
    @JsonValue
    public String toString()
    {
        return code;
    }
}
