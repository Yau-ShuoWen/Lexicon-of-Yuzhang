package com.shuowen.yuzong.Tool.dataStructure.option;

public enum Scheme
{
    DISPLAY,
    KEYBOARD,
    DEBUG;

    public static Scheme of(int code)
    {
        return switch (code)
        {
            case 1 -> DISPLAY;
            case 2 -> KEYBOARD;
            default -> throw new IllegalArgumentException("初始化范围是1~2");
        };
    }
}