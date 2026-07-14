package com.shuowen.yuzong.util.ext.set;

import java.util.*;

public class HashMultiSet<T> implements Iterable<T>
{
    private final Map<T, Integer> map;
    private int size = 0;

    public HashMultiSet()
    {
        this.map = new HashMap<>();
    }

    public void add(T value)
    {
        map.merge(value, 1, Integer::sum);
        size++;
    }

    public boolean remove(T value)
    {
        Integer cnt = map.get(value);

        if (cnt == null) return false;

        if (cnt == 1) map.remove(value);
        else map.put(value, cnt - 1);

        size--;
        return true;
    }

    public int count(T value)
    {
        return map.getOrDefault(value, 0);
    }

    public int size()
    {
        return size;
    }

    public int distinctSize()
    {
        return map.size();
    }

    @Override
    public Iterator<T> iterator()
    {
        return new MultiSetIterator<>(map.entrySet().iterator());
    }
}