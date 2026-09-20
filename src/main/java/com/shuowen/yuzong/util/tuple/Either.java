package com.shuowen.yuzong.util.tuple;

import com.shuowen.yuzong.util.ext.other.NullTool;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 表示两个互斥结果之一：左值或右值。
 *
 * @param <L> 左值类型
 * @param <R> 右值类型
 */
public final class Either<L, R>
{
    private final boolean left;
    private final L leftValue;
    private final R rightValue;

    private Either(boolean left, L leftValue, R rightValue)
    {
        this.left = left;
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public static <L, R> Either<L, R> left(L value)
    {
        NullTool.checkNotNull(value, "Either 的左值不能为空");
        return new Either<>(true, value, null);
    }

    public static <L, R> Either<L, R> right(R value)
    {
        NullTool.checkNotNull(value, "Either 的右值不能为空");
        return new Either<>(false, null, value);
    }

    /**
     * 优先选择左值；左值为 {@code null} 时选择右值。
     * <p>
     * 两个值均由调用方先计算完成，因此此方法只负责选择，不能阻止右值的计算。
     * 两边均为 {@code null} 时返回 {@link Maybe#nothing()}。
     */
    public static <L, R> Maybe<Either<L, R>> firstNonNull(L left, R right)
    {
        if (left != null) return Maybe.exist(Either.<L, R>left(left));
        return right != null ? Maybe.exist(Either.<L, R>right(right)) : Maybe.nothing();
    }

    /**
     * 惰性地获取第一个非 {@code null} 的结果。
     * <p>
     * 左函数返回非 {@code null} 时，右函数不会执行；左函数返回 {@code null} 时才执行右函数。
     * 两个函数均返回 {@code null} 时返回 {@link Maybe#nothing()}。
     * 函数抛出的异常会原样向外传播，不会被视为未命中，也不会继续执行右函数。
     */
    public static <L, R> Maybe<Either<L, R>> firstNonNull(Supplier<? extends L> leftSupplier,
                                                           Supplier<? extends R> rightSupplier)
    {
        NullTool.checkNotNull(leftSupplier, "左值函数不能为空");
        NullTool.checkNotNull(rightSupplier, "右值函数不能为空");

        L leftValue = leftSupplier.get();
        if (leftValue != null) return Maybe.exist(Either.<L, R>left(leftValue));

        R rightValue = rightSupplier.get();
        return rightValue != null ? Maybe.exist(Either.<L, R>right(rightValue)) : Maybe.nothing();
    }

    public boolean isLeft()
    {
        return left;
    }

    public boolean isRight()
    {
        return !left;
    }

    public L getLeft()
    {
        if (isRight()) throw new IllegalStateException("当前 Either 保存的是右值，不能获取左值");
        return leftValue;
    }

    public R getRight()
    {
        if (isLeft()) throw new IllegalStateException("当前 Either 保存的是左值，不能获取右值");
        return rightValue;
    }

    public L getLeftOrDefault(L defaultValue)
    {
        NullTool.checkNotNull(defaultValue, "默认左值不能为空");
        return isLeft() ? leftValue : defaultValue;
    }

    public R getRightOrDefault(R defaultValue)
    {
        NullTool.checkNotNull(defaultValue, "默认右值不能为空");
        return isRight() ? rightValue : defaultValue;
    }

    public <T> T fold(Function<? super L, ? extends T> leftHandler,
                      Function<? super R, ? extends T> rightHandler)
    {
        NullTool.checkNotNull(leftHandler, "左值处理函数不能为空");
        NullTool.checkNotNull(rightHandler, "右值处理函数不能为空");
        return isLeft() ? leftHandler.apply(leftValue) : rightHandler.apply(rightValue);
    }

    public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper)
    {
        NullTool.checkNotNull(mapper, "左值转换函数不能为空");
        return isLeft() ? Either.left(mapper.apply(leftValue)) : Either.right(rightValue);
    }

    public <T> Either<L, T> mapRight(Function<? super R, ? extends T> mapper)
    {
        NullTool.checkNotNull(mapper, "右值转换函数不能为空");
        return isRight() ? Either.right(mapper.apply(rightValue)) : Either.left(leftValue);
    }

    public Either<R, L> swap()
    {
        return isLeft() ? Either.right(leftValue) : Either.left(rightValue);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Either<?, ?> other) || left != other.left) return false;
        return left ? Objects.equals(leftValue, other.leftValue) : Objects.equals(rightValue, other.rightValue);
    }

    @Override
    public int hashCode()
    {
        return left ? Objects.hash(true, leftValue) : Objects.hash(false, rightValue);
    }

    @Override
    public String toString()
    {
        return left ? "Either.left(" + leftValue + ")" : "Either.right(" + rightValue + ")";
    }
}
