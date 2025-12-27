package com.shuowen.yuzong.Tool.TestTool;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;

import java.util.*;

public class EqualChecker<T>
{
    Counter counter = new Counter();
    List<Pair<T, T>> log = new ArrayList<>();

    /**
     * 一般情况下，只有错误的值需要做额外的处理，所以在这里反转结果，一般的用法如下
     * <blockquote><pre>
     *     if(check(检查相等)) 错误处理流程
     * </pre></blockquote>
     */
    public boolean check(T a, T b)
    {
        if (counter.check(a.equals(b)))
        {
            log.add(Pair.of(a, b));
            return true;
        }
        return false;
    }

    public String info()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(counter.info());
        for (var i : log) sb.append("\t").append(i.getLeft()).append("   ").append(i.getRight());
        return sb.toString();
    }

    public void report()
    {
        System.out.println(info());
    }

    public boolean allTrue()
    {
        return log.isEmpty();
    }

    @Override
    public String toString()
    {
        if (allTrue()) return "通过所有测试，全部相等";
        else return info();
    }
}
