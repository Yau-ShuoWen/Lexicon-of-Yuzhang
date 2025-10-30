package com.shuowen.yuzong.Tool.dataStructure.tuple;

import lombok.Data;

@Data
public class Triple<T, U, V>
{
    private T left;
    private U middle;
    private V right;

    public Triple(T left, U middle, V right)
    {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public static <T, U, V> Triple<T, U, V> of(T left, U middle, V right)
    {
        return new Triple<>(left, middle, right);
    }
}
