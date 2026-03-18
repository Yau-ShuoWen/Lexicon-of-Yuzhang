package com.shuowen.yuzong.Tool.format;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * 对字符串做简单的62进制编码
 */
@EqualsAndHashCode
public class ObfString
{
    private final char[] CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private final BigInteger BASE = BigInteger.valueOf(62);

    @Getter
    private final String code;

    private ObfString(String input, boolean encode)
    {
        if (encode)
        {
            byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

            BigInteger num = new BigInteger(1, bytes); // 1 = 正数
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
        else this.code = input;
    }

    /**
     * Spring Boot框架、Jackson库和手动反序列化入口
     */
    @JsonCreator
    public static ObfString valueOf(String code)
    {
        validate(code);
        return new ObfString(code, false);
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
    public static ObfString encode(String input)
    {
        return new ObfString(input, true);
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
     * 解码为字符串
     */
    public String decode()
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

        byte[] bytes = num.toByteArray();

        if (bytes.length > 1 && bytes[0] == 0)
        {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
