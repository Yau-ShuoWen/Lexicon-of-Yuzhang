package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;
import java.util.function.Function;

public class ListTool
{
    public static <T, U> List<U> mapping(List<T> list, Function<T, U> mapper)
    {
        NullTool.checkNotNull(list);
        List<U> result = new ArrayList<>(list.size());
        for (T i : list) result.add(mapper.apply(i));
        return result;
    }

    public static <T> List<T> filter(List<T> list, Function<T, Boolean> check)
    {
        NullTool.checkNotNull(list);
        List<T> result = new ArrayList<>(list.size());
        for (T i : list) if (check.apply(i)) result.add(i);
        return result;
    }
}
