package com.shuowen.yuzong.Tool.dataStructure;

import lombok.Data;

import java.util.Iterator;

@Data
public class Range implements Iterable<Integer>
{
    private final int start;
    private final int end;
    private final int step;

    public Range(int start, int end, int step)
    {
        if (step == 0) throw new IllegalArgumentException("步长不能为0");
        this.start = start;
        this.end = end;
        this.step = step;
    }

    public static Range of(int end)
    {
        return new Range(0, end, 1);
    }

    public static Range of(int start, int end)
    {
        return new Range(start, end, 1);
    }

    public static Range of(int start, int end, int step)
    {
        return new Range(start, end, step);
    }

    public static Range close(int end)
    {
        return new Range(0, end + 1, 1);
    }

    public static Range close(int start, int end)
    {
        return new Range(start, end + 1, 1);
    }

    public static Range close(int start, int end, int step)
    {
        return new Range(start, end + 1, step);
    }

    public boolean contains(int value)
    {
        if (step > 0)
        {
            if (value < start || value >= end) return false;
        }
        else
        {
            if (value > start || value <= end) return false;
        }
        return (value - start) % step == 0;
    }

    @Override
    public Iterator<Integer> iterator()
    {
        return new Iterator<>()
        {
            private int current = start;

            @Override
            public boolean hasNext()
            {
                return step > 0 ? current < end : current > end;
            }

            @Override
            public Integer next()
            {
                int value = current;
                current += step;
                return value;
            }
        };
    }
}