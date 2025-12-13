package com.shuowen.yuzong.data.domain.IPA;

public enum Phonogram
{
    AllPinyin,  // 全部拼音
    PinyinIPA,  // 内容部分使用拼音，参考资料用国际音标
    AllIPA;     // 全部使用使用国际音标

    Phonogram()
    {
    }

    public static Phonogram of(int code)
    {
        return switch (code)
        {
            case 1 -> AllPinyin;
            case 2 -> PinyinIPA;
            case 3 -> AllIPA;
            default -> throw new IllegalArgumentException("初始化范围是1~3");
        };
    }
}
