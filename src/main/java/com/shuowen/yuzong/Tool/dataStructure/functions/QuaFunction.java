package com.shuowen.yuzong.Tool.dataStructure.functions;

@FunctionalInterface
public interface QuaFunction<T, U, V, R, S>
{
    S apply(T t, U u, V v, R r);
}