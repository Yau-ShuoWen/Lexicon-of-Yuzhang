package com.shuowen.yuzong.data.domain.IPA;

public enum PinyinMode
{
    INTRODUCE,
    STANDARD,  // 全部拼音
    PROFESSIONAL;  // 内容部分使用拼音，参考资料用国际音标

    PinyinMode()
    {
    }

    public static PinyinMode of(int code)
    {
        return switch (code)
        {
            case 0 -> INTRODUCE;
            case 1 -> STANDARD;
            case 2 -> PROFESSIONAL;
            default -> throw new IllegalArgumentException("初始化范围是1~2");
        };
    }
}
