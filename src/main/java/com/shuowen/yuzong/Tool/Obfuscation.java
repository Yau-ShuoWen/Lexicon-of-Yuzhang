package com.shuowen.yuzong.Tool;

import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class Obfuscation
{
    private static final char[] CHARSET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final BigInteger BASE = BigInteger.valueOf(62);
    private static final Integer OFFSET = 1000;
    private static final Integer GAP = 5;

    private Obfuscation()
    {
    }

    /**
     * 对字符串做简单的62进制编码
     */
    public static String encode(String input)
    {
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

        BigInteger num = new BigInteger(1, bytes); // 1 = 正数
        if (num.equals(BigInteger.ZERO)) return "0";

        StringBuilder sb = new StringBuilder();
        while (num.compareTo(BigInteger.ZERO) > 0)
        {
            BigInteger[] divRem = num.divideAndRemainder(BASE);
            sb.append(CHARSET[divRem[1].intValue()]);
            num = divRem[0];
        }
        return sb.reverse().toString();
    }

    /**
     * 对字符串做简单的62进制解码
     */
    public static String decode(String base62)
    {
        BigInteger num = BigInteger.ZERO;

        Function<Character, Integer> fun = c ->
        {
            if (NumberTool.closeBetween(c, '0', '9')) return c - '0';
            if (NumberTool.closeBetween(c, 'A', 'Z')) return c - 'A' + 10;
            if (NumberTool.closeBetween(c, 'a', 'z')) return c - 'a' + 36;
            throw new IllegalArgumentException("Invalid Base62 char: " + c);
        };

        for (char c : base62.toCharArray())
            num = num.multiply(BASE).add(BigInteger.valueOf(fun.apply(c)));

        byte[] bytes = num.toByteArray();

        // BigInteger 可能多一个符号位 0x00
        if (bytes.length > 1 && bytes[0] == 0)
        {
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, tmp, 0, tmp.length);
            bytes = tmp;
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static String encodeInt(int input)
    {
        return encode(String.valueOf(input * GAP + OFFSET));
    }

    private static int decodeInt(String base62)
    {
        return (Integer.parseInt(decode(base62)) - OFFSET) / GAP;
    }

    public static void testString(String input)
    {
        var encoded = encode(input);
        var decoded = decode(encoded);

        ObjectTool.println("测试字符串", "原字符串：" + input, "新字符串：" + encoded, "解码结果：" + decoded);
    }

    public static void testInt(int input)
    {
        var encoded = encodeInt(input);
        var decoded = decodeInt(encoded);

        ObjectTool.println("测试整数", "原字符串：" + input, "新字符串：" + encoded, "解码结果：" + decoded);
    }
}
