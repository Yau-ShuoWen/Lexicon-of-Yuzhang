package com.shuowen.yuzong.Tool.dataStructure;


/**
 * 三种合适的参数（不考虑地区如陆港台）
 * SC 简体中文
 * TC 繁体中文
 * CH 不区分简繁
 */
public enum Language
{
    SC("sc"),
    TC("tc"),
    CH("ch");

    private final String code;

    Language(String code)
    {
        this.code = code;
    }

    /**
     * 替代原来的构造方法，提供默认值"ch"
     */
    public static Language of(String s)
    {
        if (s == null) return CH;

        s = s.trim().toLowerCase();
        for (var l : values())
            if (l.code.equals(s))
                return l;
        return CH;
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
            default -> CH;
        };
    }

    @Override
    public String toString()
    {
        return code;
    }

    public boolean isSC()
    {
        return this == SC;
    }

    public boolean isTC()
    {
        return this == TC;
    }

    public boolean isCH()
    {
        return this == CH;
    }
}

