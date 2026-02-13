package com.shuowen.yuzong.Tool.dataStructure.tuple;

import lombok.Data;

import java.util.function.Function;

@Data
public class Twin<T>
{
    private T left;
    private T right;

    public Twin(T left, T right)
    {
        this.left = left;
        this.right = right;
    }

    public static <T> Twin<T> of(T left, T right)
    {
        return new Twin<>(left, right);
    }

    public <U> Twin<U> map(Function<T, U> fun)
    {
        return Twin.of(fun.apply(left), fun.apply(right));
    }
}
