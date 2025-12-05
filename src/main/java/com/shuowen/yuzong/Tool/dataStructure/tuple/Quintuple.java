package com.shuowen.yuzong.Tool.dataStructure.tuple;

import lombok.Data;

@Data
public class Quintuple<T, U, V, W, X>
{
    private T alpha;
    private U beta;
    private V gamma;
    private W delta;
    private X epsilon;

    public Quintuple(T alpha, U beta, V gamma, W delta, X epsilon)
    {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.delta = delta;
        this.epsilon = epsilon;
    }

    public static <T, U, V, W, X> Quintuple<T, U, V, W, X>
    of(T alpha, U beta, V gamma, W delta, X epsilon)
    {
        return new Quintuple<>(alpha, beta, gamma, delta, epsilon);
    }
}
