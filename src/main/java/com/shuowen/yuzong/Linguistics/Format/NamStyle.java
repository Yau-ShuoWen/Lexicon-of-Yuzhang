package com.shuowen.yuzong.Linguistics.Format;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode (callSuper = true)
@Data
public class NamStyle extends PinyinStyle
{
    public NamStyle(int yu, int gn, int ee, int oe, int ii,
                    int ptk, int alt, int capital, int num)
    {
        this.yu = yu;
        this.gn = gn;
        this.ee = ee;
        this.oe = oe;
        this.ii = ii;
        this.ptk = ptk;
        this.alt = alt;
        this.capital = capital;
        this.num = num;
    }

    protected int yu;
    protected int gn;
    protected int ee;
    protected int oe;
    protected int ii;
    protected int ptk;
    protected int alt;
    protected int capital;
    protected int num;


    /**
     * 使用附标内容的版本
     */
    public static NamStyle getStandardStyle()
    {
        return new NamStyle(1, 0, 1, 1, 1, 0, 0, 0, 1);
    }

    /**
     * 只用上二十六字母和阿拉伯数字的版本，用于输入
     */
    public static NamStyle getKeyboardStyle()
    {
        return new NamStyle(0, 0, 0, 0, 0, 0, 0, 0, 2);
    }
}
