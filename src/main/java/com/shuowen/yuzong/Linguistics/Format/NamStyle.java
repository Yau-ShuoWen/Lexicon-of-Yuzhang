package com.shuowen.yuzong.Linguistics.Format;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode (callSuper = true)
@Data
public class NamStyle extends PinyinStyle
{
    public NamStyle()
    {
    }

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

    public static NamStyle createStyle(PinyinParam p)
    {
        int capital = switch (p.capital)
        {
            case LOWER -> 0;
            case UPPER -> 1;
            case FIRST -> 2;
        };
        return switch (p.scheme)
        {
            case KEYBOARD -> new NamStyle(0, 0, 0, 0, 0, 0, 0, capital, 2);
            case STANDARD -> new NamStyle(1, 0, 1, 1, 1, 0, 0, capital, 1);
        };
    }
}
