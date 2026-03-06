package com.shuowen.yuzong.Tool.dataStructure.tuple;

import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Data
public class Trio<T>
{
    private T left;
    private T middle;
    private T right;

    public Trio()
    {
    }

    public Trio(T left, T middle, T right)
    {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public static <T> Trio<T> of(T left, T middle, T right)
    {
        return new Trio<>(left, middle, right);
    }

    public Triple<T, T, T> toTriple()
    {
        return new Triple<>(left, middle, right);
    }

    public List<T> toList()
    {
        return List.of(left, middle, right);
    }

    public <U> Trio<U> map(Function<T, U> fun)
    {
        return Trio.of(fun.apply(left), fun.apply(middle), fun.apply(right));
    }

    public boolean all(Predicate<T> fun)
    {
        return fun.test(left) && fun.test(middle) && fun.test(right);
    }
}
