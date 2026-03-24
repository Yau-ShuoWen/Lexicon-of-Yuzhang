package com.shuowen.yuzong.Tool.dataStructure.tuple;

import lombok.Data;

@Data
public class Quadruple<T, U, V, W>
{
    private T first;
    private U second;
    private V third;
    private W fourth;

    public Quadruple()
    {
    }

    public Quadruple(T first, U second, V third, W fourth)
    {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public static <T, U, V, W> Quadruple<T, U, V, W> of(T first, U second, V third, W fourth)
    {
        return new Quadruple<>(first, second, third, fourth);
    }
}