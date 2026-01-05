package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class CollectionTool
{
    private CollectionTool()
    {
    }

    public static <T, U, C extends Collection<U>> C mapping(
            Collection<? extends T> source,
            Function<? super T, ? extends U> mapper,
            C target)
    {
        NullTool.checkNotNull(source);
        for (T i : source) target.add(mapper.apply(i));
        return target;
    }

    public static <T> void filter(Collection<T> collection, Predicate<? super T> predicate)
    {
        NullTool.checkNotNull(collection);
        collection.removeIf(predicate.negate());
    }

    /**
     * 当业务流程「按道理说」应该得到一个元素，但在不确定情况下，实际结果可能是 0 个或多个，就可以使用这个方法来校验。
     */
    public static <T> T checkSizeOne(
            Collection<? extends T> collection, String ifSmaller, String ifLarger)
    {
        NullTool.checkNotNull(collection);
        if (collection.size() == 1)
            return collection.iterator().next();

        throw new IllegalArgumentException(
                collection.isEmpty() ? ifSmaller : ifLarger
        );
    }
}
