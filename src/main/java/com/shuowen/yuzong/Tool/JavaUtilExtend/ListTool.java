package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListTool
{
    public static <T, U> List<U> mapping(Collection<? extends T> collection, Function<? super T, ? extends U> mapper)
    {
        return CollectionTool.mapping(collection, mapper, new ArrayList<>(collection.size()));
    }

    public static <T> void filter(Collection<T> collection, Predicate<? super T> predicate)
    {
        CollectionTool.filter(collection, predicate);
    }

    public static <T> T checkSizeOne(Collection<? extends T> collection, String ifSmaller, String ifLarger)
    {
        return CollectionTool.checkSizeOne(collection, ifSmaller, ifLarger);
    }
}
