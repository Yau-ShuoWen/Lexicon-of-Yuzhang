package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class SetTool
{
    private SetTool()
    {
    }

    public static <T, U> Set<U> mapping(Collection<T> set, Function<T, U> fun)
    {
        NullTool.checkNotNull(set);
        Set<U> result = new HashSet<>(set.size());
        for (T i : set) result.add(fun.apply(i));
        return result;
    }

    public static <T> Set<T> filter(Collection<T> set, Predicate<T> fun)
    {
        NullTool.checkNotNull(set);
        Set<T> result = new HashSet<>(set.size());
        for (T i : set) if (fun.test(i)) result.add(i);
        return result;
    }
}
