package com.shuowen.yuzong.Tool.dataStructure;


import lombok.Data;

@Data
public class Quadruple<T, U, V, W>
{
    private T n1;
    private U n2;
    private V n3;
    private W n4;

    public Quadruple(T n1, U n2, V n3, W n4)
    {
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        this.n4 = n4;
    }
    public static <T, U, V, W> Quadruple<T, U, V, W> of(T n1, U n2, V n3, W n4) {
        return new Quadruple<>(n1, n2, n3, n4);
    }
}