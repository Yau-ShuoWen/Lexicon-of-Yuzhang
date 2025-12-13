package com.shuowen.yuzong.data.domain.IPA;

public enum IPASyllableStyle
{
    CHINESE_SPECIAL,  // 汉语语言学的习惯用符号
    STANDARD_IPA;     // 标准的国际音标用符号

    IPASyllableStyle()
    {
    }

    public static IPASyllableStyle of(int code)
    {
        return switch (code)
        {
            case 1 -> CHINESE_SPECIAL;
            case 2 -> STANDARD_IPA;
            default -> throw new IllegalArgumentException("初始化范围是1~2");
        };
    }
}
