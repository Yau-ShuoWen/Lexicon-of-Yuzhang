package com.shuowen.yuzong.Tool.dataStructure.option;


import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;

/**
 * 两种合适的参数（不考虑地区陆港台）
 * <ul>
 * <li> SC 简体中文 </li>
 * <li> TC 繁体中文 </li>
 * </ul>
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
            default -> throw new IllegalArgumentException("要么是简体，要么是繁体");
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
        return this == SC ? "sc" : "tc";
    }
}

