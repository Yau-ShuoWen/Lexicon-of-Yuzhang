package com.shuowen.yuzong.Tool.dataStructure.tuple;

import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public Pair<T, T> toPair()
    {
        return Pair.of(left, right);
    }

    public List<T> toList()
    {
        return List.of(left, right);
    }

    public <U> Twin<U> map(Function<T, U> fun)
    {
        return Twin.of(fun.apply(left), fun.apply(right));
    }

    public boolean both(Predicate<T> fun)
    {
        return fun.test(left) && fun.test(right);
    }

    public void swap()
    {
        T tmp = left;
        left = right;
        right = tmp;
    }

}
