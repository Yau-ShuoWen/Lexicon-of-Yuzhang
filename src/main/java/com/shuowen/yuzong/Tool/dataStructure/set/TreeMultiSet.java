package com.shuowen.yuzong.Tool.dataStructure.set;

import java.util.*;

public class TreeMultiSet<T> implements Iterable<T>
{
    private final NavigableMap<T, Integer> map;

    public TreeMultiSet()
    {
        this.map = new TreeMap<>();
    }

    public TreeMultiSet(Comparator<? super T> comparator)
    {
        this.map = new TreeMap<>(comparator);
    }

    public void add(T value)
    {
        map.merge(value, 1, Integer::sum);
    }

    public boolean remove(T value)
    {
        Integer cnt = map.get(value);

        if (cnt == null) return false;

        if (cnt == 1) map.remove(value);
        else map.put(value, cnt - 1);

        return true;
    }

    public int count(T value)
    {
        return map.getOrDefault(value, 0);
    }

    @Override
    public Iterator<T> iterator()
    {
        return new MultiSetIterator<>(map.entrySet().iterator());
    }
}