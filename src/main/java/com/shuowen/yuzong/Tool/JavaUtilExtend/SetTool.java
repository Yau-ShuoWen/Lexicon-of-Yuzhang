package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;
import java.util.function.Function;

public class SetTool
{
    public static <T, U> Set<U> mapping(Set<T> list, Function<T, U> mapper)
    {
        NullTool.checkNotNull(list);
        Set<U> result = new HashSet<>(list.size());
        for (T i : list) result.add(mapper.apply(i));
        return result;
    }

    public static <T> Set<T> filter(Set<T> list, Function<T, Boolean> check)
    {
        NullTool.checkNotNull(list);
        Set<T> result = new HashSet<>(list.size());
        for (T i : list) if (check.apply(i)) result.add(i);
        return result;
    }
}
