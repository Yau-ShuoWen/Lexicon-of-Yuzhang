package com.shuowen.yuzong.Tool.TestTool;

import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;

/**
 * 一般情况下，只有错误的值需要做额外的处理，所以在这里反转结果，一般的用法如下
 * <blockquote><pre>
 *     if(check(检查相等)) 错误处理流程
 * </pre></blockquote>
 */
public class EqualChecker<T>
{
    Counter counter = new Counter();
    @Getter
    List<Pair<T, T>> log = new ArrayList<>();

    public boolean check(T a, T b)
    {
        if (counter.check(a.equals(b)))
        {
            log.add(Pair.of(a, b));
            return true;
        }
        return false;
    }

    /**
     * 直接失败
     */
    private boolean fail(T a)
    {
        counter.check(false);
        log.add(Pair.of(a, null));
        return true; // 翻转结果
    }

    /**
     * 对于一个有无值检查，取不到值，就直接增加错误记录 {@code {a, null}}
     */
    public boolean check(T a, Maybe<T> b)
    {
        if (b.isValid()) return check(a, b.getValue());
        else return fail(a);
    }

    /**
     * 对于一个有无值的成员检查，取不到值，就直接增加错误记录 {@code {a, null}}
     */
    public <U> boolean check(T a, Maybe<U> b, Function<U, T> f)
    {
        if (b.isValid()) return check(a, f.apply(b.getValue()));
        else return fail(a);
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
        System.out.println();
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
