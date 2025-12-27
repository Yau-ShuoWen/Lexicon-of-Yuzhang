package com.shuowen.yuzong.Tool.dataStructure.option;

public enum Capital
{
    LOWER,
    UPPER,
    FIRST;

    public static Capital of(int code)
    {
        return switch (code)
        {
            case 1 -> LOWER;
            case 2 -> UPPER;
            case 3 -> FIRST;
            default -> throw new IllegalArgumentException("初始化范围是1~3");
        };
    }
}