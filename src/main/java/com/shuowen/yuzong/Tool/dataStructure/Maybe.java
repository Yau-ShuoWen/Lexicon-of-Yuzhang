package com.shuowen.yuzong.Tool.dataStructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Function;

@EqualsAndHashCode
public class Maybe<T>
{
    @Getter
    private final boolean empty;
    @JsonInclude (JsonInclude.Include.NON_NULL)
    private final T value;

    private Maybe(boolean empty, T value)
    {
        this.empty = empty;
        this.value = value;
    }

    /**
     * 明确有参数的构造
     */
    public static <T> Maybe<T> exist(T value)
    {
        NullTool.checkNotNull(value, "明确有参数的构造不能再包含null了。Can not be null.");
        return new Maybe<>(false, value);
    }

    /**
     * 明确无参数的构造
     */
    public static <T> Maybe<T> nothing()
    {
        return new Maybe<>(true, null);
    }

    /**
     * 不确定是否有内容的构造
     */
    public static <T> Maybe<T> uncertain(T value)
    {
        return value == null ? nothing() : exist(value);
    }

    /**
     * 对于更多情况下反转判断的快速处理
     */
    public boolean isValid()
    {
        return !isEmpty();
    }

    /**
     * 安全的获得值
     */
    public T getValue()
    {
        NullTool.checkNotNull(value, "这个对象没有值，不可以获取。Can not get.");
        return value;
    }

    /**
     * 比较两个是否都有值并且值相等
     */
    public static <T> boolean allValidAndEqual(Maybe<T> a, Maybe<T> b)
    {
        return a.isValid() && b.isValid() && Objects.equals(a.getValue(), b.getValue());
    }

    /**
     * 如果里面有内容，就把这个内容转换成新的
     */
    public <U> Maybe<U> handleIfExist(Function<T, U> fun)
    {
        return isValid() ? Maybe.exist(fun.apply(value)) : Maybe.nothing();
    }

    @Override
    public String toString()
    {
        var s = isValid() ? "value=" + value : "empty=" + empty;
        return "Maybe(" + s + ")";
    }
}
