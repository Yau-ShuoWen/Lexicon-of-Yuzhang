package com.shuowen.yuzong.Tool.JavaUtilExtend;

import lombok.Getter;

import java.util.*;
import java.util.function.Function;

/**
 * 一个自动去重的List，主要用于搜索结果保留顺序的去重。其他功能暂无需求
 */
public class UniqueList<T, U>
{
    @Getter
    private final List<T> list = new ArrayList<>();
    private final Set<U> set = new HashSet<>();
    private final Function<T, U> keyGetter;

    @SuppressWarnings("unchecked")
    private U getKey(T item)
    {
        return keyGetter == null ? (U) item : keyGetter.apply(item);
    }

    private UniqueList()
    {
        this(null);
    }

    private UniqueList(Function<T, U> keyGetter)
    {
        this.keyGetter = keyGetter;
    }

    public static <T> UniqueList<T, T> of()
    {
        return new UniqueList<>();
    }

    public static <T,U> UniqueList<T, U> of(Function<T, U> keyGetter)
    {
        return new UniqueList<>(keyGetter);
    }

    public boolean add(T item)
    {
        U key = getKey(item);
        if (set.contains(key)) return false;
        set.add(key);
        list.add(item);
        return true;
    }

    public int addAll(Collection<? extends T> items)
    {
        if (items == null) return 0;
        int count = 0;
        for (T item : items)
        {
            if (add(item)) count++;
        }
        return count;
    }
}
