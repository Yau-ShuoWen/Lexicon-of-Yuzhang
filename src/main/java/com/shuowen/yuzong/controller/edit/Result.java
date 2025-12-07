package com.shuowen.yuzong.controller.edit;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class Result<T>
{
    private final boolean empty;
    @JsonInclude (JsonInclude.Include.NON_NULL)
    private final T value;

    private Result(boolean empty, T value)
    {
        this.empty = empty;
        this.value = value;
    }

    public static <T> Result<T> some(T value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("Result.some() cannot contain null");
        }
        return new Result<>(false, value);
    }

    public static <T> Result<T> none()
    {
        return new Result<>(true, null);
    }

    public static <T> Result<T> ofNullable(T value)
    {
        return value == null ? none() : some(value);
    }
}
