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

    /**
     * 替代原来的构造方法，提供默认值"ch"
     */
    public static IPAToneStyle of(int s)
    {
        for (var l : values())
            if (l.code == s) return l;

        return FIVE_DEGREE_LINE;
    }
    
    public boolean isFiveDegreeNum()
    {
        return code == FIVE_DEGREE_NUM.code;
    }
    
    public boolean isFiveDegreeLine()
    {
        return code == FIVE_DEGREE_LINE.code;
    }
    
    public boolean isFourCorner()
    {
        return code == FOUR_CORNER.code;
    }
}
