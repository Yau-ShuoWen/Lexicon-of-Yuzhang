package com.shuowen.yuzong.util.text;

import static com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool.checkNotNull;

/**
 * 固定长度字符串 Fixed Length Text<br>
 * 用法：传入字符串和预期长度，如果符合，则返回这个字符串，否则抛出异常
 */
public class FLText
{
    public static String of(String s, int len)
    {
        checkNotNull(s);
        if (s.length() != len) throw new IllegalArgumentException(
                String.format("预期长度：%s；实际长度：%s\n字符串：%s", len, s.length(), s)
        );
        return s;
    }
}
