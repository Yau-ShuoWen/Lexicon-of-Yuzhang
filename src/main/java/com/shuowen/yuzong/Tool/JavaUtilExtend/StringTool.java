package com.shuowen.yuzong.Tool.JavaUtilExtend;

import com.shuowen.yuzong.Tool.dataStructure.Range;
import com.shuowen.yuzong.Tool.dataStructure.error.IllegalStringException;

import java.util.*;

/**
 * 字符串的扩展函数
 */
public class StringTool
{
    private StringTool()
    {
    }

    // 检查字符串的合法性 -------------------------------------------------------------------------------------------------

    /**
     * 检查字符串是否有效：不是{@code null}，也不是空字符串{@code ""}
     */
    public static boolean isValid(String str)
    {
        return str != null && !str.isEmpty();
    }

    /**
     * @throws IllegalStringException 如果存在{@code null} 或者 空字符串{@code ""}，抛出异常
     */
    public static void checkValid(String... str)
    {
        List<String> log = new ArrayList<>();
        for (String s : str) if (!isValid(s)) log.add(s);

        if (!log.isEmpty())
            throw new IllegalStringException(String.format("""
                    异常：字符串为null，或者字符串为空。String is null or empty.
                    内容：%s
                    """, log)
            );
    }

    /**
     * 检查字符串是否有效：不是{@code null}、不是{@code ""}、不是全空格串{@code "  "}
     */
    public static boolean isTrimValid(String str)
    {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * @throws IllegalStringException 如果存在{@code null}、空字符串{@code ""}或者全空格串{@code "  "}，抛出异常
     */
    public static void checkTrimValid(String... str)
    {
        List<String> log = new ArrayList<>();
        for (String s : str) if (!isTrimValid(s)) log.add(s);

        if (!log.isEmpty())
            throw new IllegalStringException(String.format("""
                    异常：字符串为null、为空或者只包含空格。String is null, empty or trimmed empty.
                    内容：%s
                    """, log)
            );
    }

    // 检查索引的合法性 -------------------------------------------------------------------------------------------------

    /**
     * 检查索引是否在字符串有效：{@code [0, length)}，为三个参数版本的简化调用
     */
    public static boolean isIndexValid(String str, int... index)
    {
        return isIndexValid(str, true, index);
    }

    /**
     * 检查索引
     *
     * @param open 是否要判定在右侧length可不可以取，true - 大部分情况右侧为开区间{@code [0, length)} false - 右侧为闭区间{@code [0, length]}（substring的参数）
     */
    public static boolean isIndexValid(String str, boolean open, int... index)
    {
        for (int i : index)
        {
            if (open) // 右侧开区间
            {
                if (!Range.of(str.length()).contains(i)) return false;
            }
            else // 右侧闭区间
            {
                if (!Range.close(str.length()).contains(i)) return false;
            }
        }
        return true;
    }

    // 字符串构造器的扩展函数

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

    // 字符串的扩展函数

    /**
     * 如果索引越界或者不对应，都返回false，不抛出异常
     */
    public static boolean charEquals(String str, int index, char expectedChar)
    {
        return isValid(str) && isIndexValid(str, index) && str.charAt(index) == expectedChar;
    }

    /**
     * 交换字符串的两个变量
     */
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
        if ((isValid(source) &&
                isIndexValid(source, true, beginIndex) &&
                isIndexValid(source, false, endIndex) &&
                beginIndex < endIndex)
        ) return source.substring(beginIndex, endIndex);
        else return "";
    }

    public static String limitLength(String str, int num, String surplus)
    {
        NullTool.checkNotNull(str);
        return str.length() > num ? str.substring(0, num) + surplus : str;
    }

    /**
     * 获取最后一个字符，避免长的变量名写出{@code theString.charAt(theString.length() - 1)}<br>
     * 空安全，空的内容可以被正确处理
     */
    public static char back(String s)
    {
        checkTrimValid(s);
        return s.charAt(s.length() - 1);
    }

    /**
     * 删掉最后一个字符，避免长的变量名写出{@code theString.substring(0, theString.length() - 1)}<br>
     * 空安全，空的内容可以被正确处理
     */
    public static String deleteBack(String s)
    {
        checkTrimValid(s);
        return s.substring(0, s.length() - 1);
    }

    /**
     * 字符串插入函数，当只有很少次数的插入，不适合另外构造字符串时使用
     */
    public static String insert(String s, int index, char ch)
    {
        return insert(s, index, String.valueOf(ch));
    }

    /**
     * 字符串插入函数，当只有很少次数的插入，不适合另外构造字符串时使用
     */
    public static String insert(String s, int index, String ch)
    {
        checkTrimValid(s);
        if (!isIndexValid(s, false, index)) throw new IndexOutOfBoundsException("插入索引为[0, length]");

        return s.substring(0, index) + ch + s.substring(index);
    }
}
