package com.shuowen.yuzong.Tool.dataStructure.option;

import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;

/**
 * 两种合适的参数（不考虑地区陆港台）
 * <ul>
 * <li> SC 简体中文 </li>
 * <li> TC 繁体中文 </li>
 * </ul>
 * 不要使用 == 来判断 Language，需要两个分开处理的时候使用{@code isSimplified()}和默认分支
 */
public enum Language
{
    SC, TC;

    public static Language of(String s)
    {
        StringTool.checkTrimValid(s);
        return switch (s.trim().toLowerCase())
        {
            case "sc" -> SC;
            case "tc" -> TC;
            default -> throw new IllegalArgumentException("简繁体代号无效：" + s);
        };
    }

    /**
     * 简体返回繁体，繁体返回简体
     */
    public Language reverse()
    {
        return switch (this)
        {
            case SC -> TC;
            case TC -> SC;
        };
    }

    @Override
    public String toString()
    {
        return switch (this)
        {
            case SC -> "sc";
            case TC -> "tc";
        };
    }

    /**
     * 一般是判断简体字
     */
    public boolean isSimplified()
    {
        return switch (this)
        {
            case SC -> true;
            case TC -> false;
        };
    }
}

