package com.shuowen.yuzong.Tool.JavaUtilExtend;

public class StringTool
{
    /**
     * 检查一个字符串是否非null，非空
     */
    public static boolean isValid(String str)
    {
        return str != null && !str.isEmpty();
    }

    public static boolean isValid(String... str)
    {
        boolean flag = true;
        for (String s : str) flag = flag && isValid(s);
        return flag;
    }

    public static boolean isTrimValid(String str)
    {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * 检查一个字符串是否非null，去除头尾空格之后非空
     */
    public static boolean isTrimValid(String... str)
    {
        boolean flag = true;
        for (var i : str) flag = flag && isTrimValid(str);
        return flag;
    }

    /**
     * 检查一个索引是否在字符串有效
     */
    public static boolean isIndexValid(String str, int index)
    {
        return index >= 0 && index < str.length();
    }

    public static boolean isIndexValid(String str, int... index)
    {
        boolean flag = true;
        for (int i : index) flag = flag && isIndexValid(str, i);
        return flag;
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
        if (!isValid(source) || !isIndexValid(source, beginIndex, endIndex) ||
                beginIndex >= endIndex) return "";

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
        beginIndex = Math.max(0, beginIndex);
        beginIndex = Math.min(beginIndex, length);

        // 调整 endIndex 到有效范围
        endIndex = Math.max(beginIndex, endIndex);
        endIndex = Math.min(endIndex, length);

        // 如果调整后没有有效的子字符串
        if (beginIndex >= endIndex) return "";

        return source.substring(beginIndex, endIndex);
    }
}
