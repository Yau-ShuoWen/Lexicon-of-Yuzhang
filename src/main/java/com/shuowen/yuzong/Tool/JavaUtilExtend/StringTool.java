package com.shuowen.yuzong.Tool.JavaUtilExtend;

/**
 * 字符串的扩展函数
 */
public class StringTool
{
    // 检查字符串的合法性

    /**
     * 检查一系列字符串，是否存在至少一个无效（null）或者为空
     *
     * @return 返回结果
     */
    public static boolean isValid(String... str)
    {
        NullTool.checkNotNull(str);
        for (String s : str) if (s == null || s.isEmpty()) return false;
        return true;
    }

    /**
     * 检查一系列字符串，是否存在至少一个无效（null）或者为空
     *
     * @throws IllegalArgumentException 存在的话就抛出异常
     */
    public static void checkValid(String... str)
    {
        if (!isValid(str)) throw new IllegalArgumentException(
                "字符串无效或者为空。String is null or empty.");
    }

    /**
     * 检查一系列字符串，是否存在至少一个无效（null）或者只包含空格
     *
     * @return 返回结果
     */
    public static boolean isTrimValid(String... str)
    {
        NullTool.checkNotNull(str);
        for (String s : str) if (s == null || s.trim().isEmpty()) return false;
        return true;
    }

    /**
     * 检查一系列字符串，是否存在至少一个无效（null）或者只包含空格
     *
     * @throws IllegalArgumentException 存在的话就抛出异常
     */
    public static void checkTrimValid(String... str)
    {
        if (!isTrimValid(str)) throw new IllegalArgumentException(
                "字符串无效、为空或者只包含空格。String is null, empty or trimmed empty.");
    }

    // 检查索引的合法性

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
                if (!NumberTool.arrayBetween(i, 0, str.length())) return false;
            }
            else // 右侧闭区间
            {
                if (!NumberTool.closeBetween(i, 0, str.length())) return false;
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
