package com.shuowen.yuzong.Tool.JavaUtilExtend;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListTool
{
    private ListTool()
    {
    }

    public static <T, U> List<U> mapping(Collection<T> list, Function<T, U> fun)
    {
        NullTool.checkNotNull(list);
        List<U> result = new ArrayList<>(list.size());
        for (T i : list) result.add(fun.apply(i));
        return result;
    }

    public static <T> List<T> filter(Collection<T> list, Predicate<T> fun)
    {
        NullTool.checkNotNull(list);
        List<T> result = new ArrayList<>(list.size());
        for (T i : list) if (fun.test(i)) result.add(i);
        return result;
    }

    /**
     * 当业务流程「按道理说」应该得到一个元素，但在不确定情况下，实际结果可能是 0 个或多个，就可以使用这个方法来校验。
     */
    public static <T> T checkSizeOne(List<T> list, String ifSmaller, String ifLarger)
    {
        NullTool.checkNotNull(list);
        if (list.size() == 1) return list.get(0);
        else throw new IllegalArgumentException(list.isEmpty() ? ifSmaller : ifLarger);
    }

    public static <T> boolean all(List<T> list, Predicate<T> fun)
    {
        NullTool.checkNotNull(list);
        for (T i : list) if (!fun.test(i)) return false;
        return true;
    }

    public static <T> boolean exist(List<T> list, Predicate<T> fun)
    {
        NullTool.checkNotNull(list);
        for (T i : list) if (fun.test(i)) return true;
        return false;
    }

    public static <T> void handle(List<T> list, Consumer<T> fun)
    {
        NullTool.checkNotNull(list);
        for (T i : list) fun.accept(i);
    }

    @SafeVarargs
    public static <T> List<T> nCopies(Pair<Integer, T>... pair)
    {
        NullTool.checkNotNull(pair);
        List<T> result = new ArrayList<>();
        for (var i:pair)
        {
            result.addAll(Collections.nCopies(i.getLeft(), i.getRight()));
        }
        return result;
    }

}