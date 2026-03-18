package com.shuowen.yuzong.Tool.format;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;

/**
 * 对整数做简单的62进制编码
 */
@EqualsAndHashCode
public class ObfInt
{
    private final char[] CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private final BigInteger BASE = BigInteger.valueOf(62);

    @Getter
    private final String code;

    private ObfInt(int input)
    {
        input = 13331 + input * 7;
        BigInteger num = BigInteger.valueOf(input);

        if (num.equals(BigInteger.ZERO)) code = "0";
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

    /**
     * Spring Boot框架、Jackson库和手动反序列化入口
     */
    @JsonCreator
    public static ObfInt valueOf(String code)
    {
        validate(code);
        return new ObfInt(code);
    }

    private static void validate(String code)
    {
        StringTool.checkValid(code);

        for (char c : code.toCharArray())
        {
            if (NumberTool.closeBetween(c, '0', '9')) continue;
            if (NumberTool.closeBetween(c, 'A', 'Z')) continue;
            if (NumberTool.closeBetween(c, 'a', 'z')) continue;

            throw new IllegalArgumentException("无效的62进制编码字符: " + c);
        }
    }

    /**
     * 后端的编码
     */
    public static ObfInt encode(int input)
    {
        return new ObfInt(input);
    }

    /**
     * 序列化
     */
    @JsonValue
    @Override
    public String toString()
    {
        return code;
    }

    /**
     * 解码为整数
     */
    public int decode()
    {
        BigInteger num = BigInteger.ZERO;

        for (char c : this.code.toCharArray())
        {
            int val = -1;

            if (NumberTool.closeBetween(c, '0', '9')) val = c - '0';
            if (NumberTool.closeBetween(c, 'A', 'Z')) val = c - 'A' + 10;
            if (NumberTool.closeBetween(c, 'a', 'z')) val = c - 'a' + 36;

            num = num.multiply(BASE).add(BigInteger.valueOf(val));
        }

        if (num.bitLength() > 31)
            throw new IllegalArgumentException("解码结果超过 int 范围: " + num);

        return (num.intValue() - 13331) / 7;
    }

}