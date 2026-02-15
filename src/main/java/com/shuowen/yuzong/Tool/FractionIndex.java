package com.shuowen.yuzong.Tool;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

/**
 * 字符串索引类。<br>
 * <p>
 * - 字符串表示等价于数值大小排序，精度可无限增长<br>
 * - 所有索引都表示 (0, 1) 区间内的小数，开区间，索引永不为 0、1<br>
 * - 因为输入的字符串是小数位，实际上只需要限制不为0即可
 */
@Data
@JsonSerialize (using = FractionIndex.FractionIndexSerializer.class)
@JsonDeserialize (using = FractionIndex.FractionIndexDeserializer.class)
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

    public static class FractionIndexDeserializer extends JsonDeserializer<FractionIndex>
    {
        @Override
        public FractionIndex deserialize(
                JsonParser p,
                DeserializationContext ctxt
        ) throws IOException
        {
            String encoded = p.getValueAsString();
            return FractionIndex.of(
                    Obfuscation.decode(encoded)
            );
        }
    }

    public static class FractionIndexSerializer extends JsonSerializer<FractionIndex>
    {
        @Override
        public void serialize(
                FractionIndex value,
                JsonGenerator gen,
                SerializerProvider serializers
        ) throws IOException
        {
            gen.writeString(
                    Obfuscation.encode(value.toString())
            );
        }
    }
}