package com.shuowen.yuzong.Tool.JavaUtilExtend;

import static java.lang.Math.max;

public class StringTool
{
    /**
     * 检查一系列字符串，是否存在至少一个无效（null）或者为空，将结果返回
     */
    public static boolean isValid(String... str)
    {
        NullTool.checkNotNull(str);
        for (String s : str) if (s == null || s.isEmpty()) return false;
        return true;
    }

    /**
     * 检查一系列字符串，是否存在至少一个无效（null）或者为空，存在的话就抛出异常
     */
    public static void checkValid(String... str)
    {
        if (!isValid(str)) throw new IllegalArgumentException(
                "字符串无效或者为空。String is null or empty.");
    }

    /**
     * 检查一系列字符串，是否存在至少一个无效（null）或者只包含空格，将结果返回
     */
    public static boolean isTrimValid(String... str)
    {
        NullTool.checkNotNull(str);
        for (String s : str) if (s == null || s.trim().isEmpty()) return false;
        return true;
    }

    /**
     * 检查一系列字符串，是否存在至少一个无效（null）或者只包含空格，存在的话就抛出异常
     */
    public static void checkTrimValid(String... str)
    {
        if (!isTrimValid(str)) throw new IllegalArgumentException(
                "字符串无效、为空或者只包含空格。String is null, empty or trimmed empty.");
    }

    /**
     * 检查索引是否在字符串有效
     */
    public static boolean isIndexValid(String str, int... index)
    {
        boolean flag = true;
        for (int i : index) flag = flag && NumberTool.arrayBetween(i, 0, str.length());
        return flag;
    }

    /**
     * 和 isIndexValid 的区别就是后一个坐标可以 ==length 的
     */
    public static boolean isTwoIndexValid(String str, int index1, int index2)
    {
        return isIndexValid(str, index1, index2 - 1) && (index1 < index2);
    }

    /**
     * 对于 StringBuilder 的一次性根据值替换
     */
    public static void replace(StringBuilder sb, String target, String replacement)
    {
        if (target.equals(replacement)) return;
        int index;
        while ((index = sb.indexOf(target)) != -1)
        {
            sb.replace(index, index + target.length(), replacement);
        }
    }

    /**
     * 如果索引越界或者不对应，都返回false，不抛出异常
     */
    public static boolean charEquals(String str, int index, char expectedChar)
    {
        return str != null
                && isIndexValid(str, index)
                && str.charAt(index) == expectedChar;
    }

    public static String swap(String str, int i, int j)
    {
        if (!isIndexValid(str, i, j)) return str;

        char[] charArray = str.toCharArray();
        char temp = charArray[i];
        charArray[i] = charArray[j];
        charArray[j] = temp;

        return new String(charArray);
    }

    /**
     * 安全的字符串剪裁，无效的会返回空
     */
    public static String substring(String source, int beginIndex)
    {
        if (!isValid(source) || !isIndexValid(source, beginIndex)) return "";

        return source.substring(beginIndex);
    }

    /**
     * 安全的字符串剪裁，无效的会返回空
     */
    public static String substring(String source, int beginIndex, int endIndex)
    {
        if (!isValid(source) || !isTwoIndexValid(source, beginIndex, endIndex)) return "";

        return source.substring(beginIndex, endIndex);
    }


    /**
     * 更宽松的版本：自动调整边界而不是返回空字符串
     */
    public static String safeSubstringLenient(String source, int beginIndex, int endIndex)
    {
        if (!isValid(source)) return "";

        int length = source.length();

        // 调整 beginIndex 到有效范围
        beginIndex = max(0, beginIndex);
        beginIndex = Math.min(beginIndex, length);

        // 调整 endIndex 到有效范围
        endIndex = max(beginIndex, endIndex);
        endIndex = Math.min(endIndex, length);

        // 如果调整后没有有效的子字符串
        if (beginIndex >= endIndex) return "";

        return source.substring(beginIndex, endIndex);
    }

    /**
     * 这个获取是空安全的，空的内容可以被正确处理
     */
    public static char back(String s)
    {
        checkTrimValid(s);
        return s.charAt(s.length() - 1);
    }

    public static String deleteBack(String s)
    {
        checkTrimValid(s);
        return s.substring(0, s.length() - 1);
    }
}
