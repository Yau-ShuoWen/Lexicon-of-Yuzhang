package com.shuowen.yuzong.Linguistics.Format;

import lombok.Data;

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

    protected int yu = 1;
    protected int gn = 0;
    protected int ee = 2;
    protected int oe = 3;
    protected int ii = 1;
    protected int ptk = 1;
    protected int alt = 0;

    public NamStyle()
    {
    }

    public NamStyle(int[] a)
    {
        if (a.length > 0) yu = a[0];
        if (a.length > 1) gn = a[1];
        if (a.length > 2) ee = a[2];
        if (a.length > 3) oe = a[3];
        if (a.length > 4) ii = a[4];
        if (a.length > 5) ptk = a[5];
        if (a.length > 6) alt = a[6];
        if (a.length > 7) capital = a[7];
        if (a.length > 8) num = a[8];
    }

    /**
     * 数据库里存的原版格式
     */
    public static NamStyle getDataBaseStyle()
    {
        return new NamStyle(0, 0, 0, 0, 0, 0, 0, 0, 3);
    }

    /**
     * 只用上二十六字母和阿拉伯数字的版本
     */
    public static NamStyle getKeyboardStyle()
    {
        return new NamStyle(2, 0, 0, 0, 1, 2, 0, 0, 1);
    }

    public static NamStyle getStandardStyle()
    {
        return new NamStyle(1, 0, 2, 2, 1, 0, 0, 0, 1);
    }
}
