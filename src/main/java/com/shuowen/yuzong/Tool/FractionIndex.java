package com.shuowen.yuzong.Tool;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.format.ObfString;
import lombok.Data;

import java.math.*;
import java.util.*;

/**
 * 字符串索引类。<br>
 * <p>
 * - 字符串表示等价于数值大小排序，精度可无限增长<br>
 * - 所有索引都表示 (0, 1) 区间内的小数，开区间，索引永不为 0、1<br>
 * - 因为输入的字符串是小数位，实际上只需要限制不为0即可
 */
@Data
public class FractionIndex implements Comparable<FractionIndex>
{
    private final BigDecimal d;

    private FractionIndex(String str)
    {
        d = new BigDecimal(str);
        if (d.compareTo(BigDecimal.ZERO) == 0) throw new IllegalArgumentException("zero");
    }

    private FractionIndex(BigDecimal d)
    {
        this.d = d;
    }

    /**
     * 井号是为了防止长得像数字被弱类型错误解读
     */
    public String toString()
    {
        return "#" + d.toString();
    }

    public int compareTo(FractionIndex other)
    {
        return d.compareTo(other.d);
    }

    /**
     * 使用不包含小数部分的字符串
     */
    public static FractionIndex fromInteger(String str)
    {
        if (!str.matches("\\d+")) throw new NumberFormatException("字符串不是纯数字");
        return new FractionIndex("0." + str);
    }

    /**
     * 可以识别整数和小数
     */
    public static FractionIndex of(String str)
    {
        if (str.startsWith("#")) str = str.substring(1);
        if (str.contains("0.")) return new FractionIndex(str);
        return FractionIndex.fromInteger(str);
    }

    /**
     * 返回一对能充分应用区间的端点值
     */
    public static Pair<FractionIndex, FractionIndex> getEndPoint()
    {
        return Pair.of(new FractionIndex("0.1"), new FractionIndex("0.9"));
    }

    /**
     * 返回终点值
     */
    public static FractionIndex getMidPoint(FractionIndex left, FractionIndex right)
    {
        var l = left.getD();
        var r = right.getD();

        if (l.compareTo(r) >= 0) throw new IllegalArgumentException("左索引应该比右边小");

        return new FractionIndex(calculateMid(l, r));
    }

    /**
     * 核心算法：选取插入点，有「空隙」就不扩位
     */
    private static BigDecimal calculateMid(BigDecimal l, BigDecimal r)
    {
        int scale = Math.max(l.scale(), r.scale());

        while (true)
        {
            BigDecimal ll = l.setScale(scale, RoundingMode.UNNECESSARY);
            BigDecimal rr = r.setScale(scale, RoundingMode.UNNECESSARY);

            BigInteger li = ll.unscaledValue();
            BigInteger ri = rr.unscaledValue();

            // ri - li > 1  ⇒ 中间至少有一个整数
            if (ri.subtract(li).compareTo(BigInteger.ONE) > 0)
            {
                BigInteger mid = li.add(ri).shiftRight(1);
                return new BigDecimal(mid, scale).stripTrailingZeros();
            }

            // 没空位，只能增加精度
            scale++;
        }
    }

    /**
     * 使用二分的递归法获取N个连续的点
     */
    public static List<FractionIndex> between(FractionIndex left, FractionIndex right, int n)
    {
        if (n < 0) throw new IllegalArgumentException("数字必须大于零。 n must be >= 0");
        if (n == 0) return List.of();
        if (n == 1) return List.of(getMidPoint(left, right));

        FractionIndex mid = getMidPoint(left, right);

        int leftCount = n / 2;
        int rightCount = n - leftCount - 1;

        List<FractionIndex> result = new ArrayList<>(n);

        result.addAll(between(left, mid, leftCount));
        result.add(mid);
        result.addAll(between(mid, right, rightCount));

        return result;
    }

    /**
     * 整理、重建索引。
     */
    public static List<FractionIndex> rebuild(int n)
    {
        // 0.(0)1  0.(99)
        var dataSize = String.valueOf(n).length();
        var first = FractionIndex.of("0." + "0".repeat(dataSize - 1) + "1");
        var end = FractionIndex.of("0." + "9".repeat(dataSize));

        List<FractionIndex> result = new ArrayList<>(n);
        result.add(first);
        result.addAll(between(first, end, n - 2));
        result.add(end);

        return result;
    }

    public static FractionIndex valueOf(String code)
    {
        // spring boot只识别String参数的内容，不可以接力，所以要创建
        return FractionIndex.of(ObfString.valueOf(code).decode());
    }

    @JsonCreator
    public static FractionIndex fromJson(ObfString code)
    {
        return FractionIndex.of(code.decode());
    }

    @JsonValue
    public ObfString toJson()
    {
        return ObfString.encode(this.toString());
    }
}