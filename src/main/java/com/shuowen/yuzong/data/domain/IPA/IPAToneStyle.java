package com.shuowen.yuzong.data.domain.IPA;

public enum IPAToneStyle
{
    FIVE_DEGREE_NUM,   //五度标记法：数字
    FIVE_DEGREE_LINE,  //五度标记法：符号
    FOUR_CORNER;       //四角调类格式

    IPAToneStyle()
    {
    }

    public static IPAToneStyle of(int code)
    {
        return switch (code)
        {
            case 1 -> FIVE_DEGREE_NUM;
            case 2 -> FIVE_DEGREE_LINE;
            case 3 -> FOUR_CORNER;
            default -> throw new IllegalArgumentException("初始化范围是1~3");
        };
    }
}
