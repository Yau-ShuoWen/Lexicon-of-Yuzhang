package com.shuowen.yuzong.util.text;

import static com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool.checkNotNull;

/**
 * 固定长度字符串 Fixed Length Text
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
