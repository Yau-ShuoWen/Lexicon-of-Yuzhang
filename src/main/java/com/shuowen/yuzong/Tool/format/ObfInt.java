package com.shuowen.yuzong.Tool.format;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;

import java.math.BigInteger;
import java.util.function.Function;

/**
 * 对整数做简单的62进制编码
 */
public class ObfInt
{
    private final char[] CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private final BigInteger BASE = BigInteger.valueOf(62);

    private final String code;

    private ObfInt(int input)
    {
        input = 13331 + input * 7;
        BigInteger num = BigInteger.valueOf(input);

        if (num.equals(BigInteger.ZERO))
        {
            code = "0";
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            while (num.compareTo(BigInteger.ZERO) > 0)
            {
                BigInteger[] divRem = num.divideAndRemainder(BASE);
                sb.append(CHARSET[divRem[1].intValue()]);
                num = divRem[0];
            }
            code = sb.reverse().toString();
        }
    }

    private ObfInt(String code)
    {
        this.code = code;
    }

    public static ObfInt valueOf(String code)
    {
        return new ObfInt(code);
    }

    @JsonCreator
    public static ObfInt encode(int input)
    {
        return new ObfInt(input);
    }

    @JsonValue
    public String getCode()
    {
        return code;
    }

    /**
     * 将 Base62 字符串解码为 int
     */
    public int decode()
    {
        BigInteger num = BigInteger.ZERO;

        Function<Character, Integer> fun = c ->
        {
            if (NumberTool.closeBetween(c, '0', '9')) return c - '0';
            if (NumberTool.closeBetween(c, 'A', 'Z')) return c - 'A' + 10;
            if (NumberTool.closeBetween(c, 'a', 'z')) return c - 'a' + 36;
            throw new IllegalArgumentException("无效的62进制编码" + c);
        };

        for (char c : this.code.toCharArray())
            num = num.multiply(BASE).add(BigInteger.valueOf(fun.apply(c)));

        if (num.bitLength() > 31)
            throw new IllegalArgumentException("解码结果超过 int 范围: " + num);

        return (num.intValue() - 13331) / 7;
    }

}