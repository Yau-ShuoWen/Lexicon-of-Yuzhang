package com.shuowen.yuzong.Tool.JavaUtilExtend;

public class StringTool
{
    public static void replace(StringBuilder sb, String target, String replacement)
    {
        if (target.equals(replacement)) return;
        int index;
        while ((index = sb.indexOf(target)) != -1)
        {
            sb.replace(index, index + target.length(), replacement);
        }
    }
}
