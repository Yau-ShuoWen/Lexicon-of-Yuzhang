package com.shuowen.yuzong.Tool.dataStructure.tuple;


import lombok.Data;

@Data
public class Quadruple<T, U, V, W>
{
    private T alpha;
    private U beta;
    private V gamma;
    private W delta;

    public Quadruple(T alpha, U beta, V gamma, W delta)
    {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.delta = delta;
    }
    public static <T, U, V, W> Quadruple<T, U, V, W> of(T alpha, U beta, V gamma, W delta) {
        return new Quadruple<>(alpha, beta, gamma, delta);
    }
}