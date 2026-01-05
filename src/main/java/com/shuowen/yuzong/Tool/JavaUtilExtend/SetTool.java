package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class SetTool
{
    public static <T, U> Set<U> mapping(Collection<? extends T> collection, Function<? super T, ? extends U> mapper)
    {
        return CollectionTool.mapping(collection, mapper, new HashSet<>(collection.size()));
    }

    public static <T> void filter(Collection<T> collection, Predicate<? super T> predicate)
    {
        CollectionTool.filter(collection, predicate);
    }
}
