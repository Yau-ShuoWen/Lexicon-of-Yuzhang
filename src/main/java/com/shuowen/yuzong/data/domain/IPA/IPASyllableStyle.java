package com.shuowen.yuzong.data.domain.IPA;

public enum IPASyllableStyle
{
    CHINESE_SPECIAL(0),
    STANDARD_IPA(1);

    private int code;

    IPASyllableStyle(int code)
    {
        this.code = code;
    }

    public static IPASyllableStyle of(int code)
    {
        for (var l : values())
            if (l.code == code) return l;
        return CHINESE_SPECIAL;
    }
}
