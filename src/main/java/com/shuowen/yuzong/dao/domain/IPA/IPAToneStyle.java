package com.shuowen.yuzong.dao.domain.IPA;

/**
 * 调类样式
 */
public enum IPAToneStyle
{
    FIVE_DEGREE_NUM(0),   //五度标记法：数字
    FIVE_DEGREE_LINE(1),  //五度标记法：符号
    FOUR_CORNER(2);       //四角调类格式：

    private int code;

    IPAToneStyle(int code)
    {
        this.code = code;
    }

    public static IPAToneStyle of(int code)
    {
        for (var l : values())
            if (l.code == code) return l;

        return FIVE_DEGREE_LINE;
    }
}
