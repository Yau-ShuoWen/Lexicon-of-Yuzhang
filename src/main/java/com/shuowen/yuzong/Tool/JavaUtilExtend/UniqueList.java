package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;


/**
 * 一个自动去重的List，主要用于搜索结果。其他功能暂无需求
 */
public class UniqueList<T>
{
    private final List<T> list = new ArrayList<>();
    private final Set<T> set = new HashSet<>();

    public UniqueList()
    {

    }

    public boolean add(T item)
    {
        if (set.contains(item)) return false;
        set.add(item);
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

    public List<T> toList()
    {
        return list;
    }
}
