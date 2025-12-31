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

    /**
     * 当业务流程「按道理说」应该得到一个元素，但在不确定情况下，实际结果可能是 0 个或多个，就可以使用这个方法来校验。
     */
    public static <T> T checkSizeOne(List<T> list, String ifSmaller, String ifLarger)
    {
        NullTool.checkNotNull(list);
        if (list.size() == 1) return list.get(0);
        else throw new IllegalArgumentException(list.isEmpty() ? ifSmaller : ifLarger);
    }
}
