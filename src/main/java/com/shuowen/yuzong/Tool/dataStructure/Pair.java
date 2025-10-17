package com.shuowen.yuzong.Tool.dataStructure;

import lombok.Data;

@Data
public class Pair<T, U>
{
    private T left;
    private U right;

    public Pair(T left, U right)
    {
        this.left = left;
        this.right = right;
    }

    public static <T, U> Pair<T, U> of(T left, U right)
    {
        return new Pair<>(left, right);
    }
}
