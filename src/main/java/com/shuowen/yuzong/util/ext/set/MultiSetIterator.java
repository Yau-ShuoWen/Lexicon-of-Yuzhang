package com.shuowen.yuzong.util.ext.set;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

class MultiSetIterator<T> implements Iterator<T>
{
    private final Iterator<Map.Entry<T, Integer>> entryIterator;

    private T currentValue;
    private int remaining = 0;

    public MultiSetIterator(Iterator<Map.Entry<T, Integer>> entryIterator)
    {
        this.entryIterator = entryIterator;
    }

    @Override
    public boolean hasNext()
    {
        return remaining > 0 || entryIterator.hasNext();
    }

    @Override
    public T next()
    {
        if (!hasNext()) throw new NoSuchElementException();

        if (remaining == 0)
        {
            Map.Entry<T, Integer> entry = entryIterator.next();
            currentValue = entry.getKey();
            remaining = entry.getValue();
        }

        remaining--;
        return currentValue;
    }
}