package com.shuowen.yuzong.Linguistics.Format;

public class CuanStyle extends StyleParams
{
    public int yu = 1, gn = 0, tz = 0, alt = 1, capital = 0,num=0;

    public CuanStyle()
    {
    }

    public CuanStyle(int[] a)
    {
        if (a.length >0) yu = a[0];
        if (a.length >1) gn = a[1];
        if (a.length >2) tz = a[2];
        if (a.length >3) alt = a[3];
        if (a.length >4) capital = a[4];
    }

    public CuanStyle(int yu, int gn, int tz, int alt, int capital)
    {
        this.yu = yu;
        this.gn = gn;
        this.tz = tz;
        this.alt = alt;
        this.capital = capital;
    }
}
