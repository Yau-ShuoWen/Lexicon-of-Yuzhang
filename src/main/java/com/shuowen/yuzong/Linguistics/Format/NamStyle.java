package com.shuowen.yuzong.Linguistics.Format;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIgnoreProperties (ignoreUnknown = true)
@EqualsAndHashCode (callSuper = true)
@Data
public class NamStyle extends PinyinStyle
{
    public NamStyle()
    {
    }

    public NamStyle(int yu, int gn, int ee, int oe, int ii, int ptk, int yw, int capital, int num, int iu)
    {
        this.yu = yu;
        this.gn = gn;
        this.ee = ee;
        this.oe = oe;
        this.ii = ii;
        this.ptk = ptk;
        this.yw = yw;
        this.capital = capital;
        this.num = num;
        this.iu = iu;
    }

    protected int yu;
    protected int gn;
    protected int ee;
    protected int oe;
    protected int ii;
    protected int ptk;
    protected int yw;
    protected int capital;
    protected int num;
    protected int iu;

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
            case KEYBOARD -> new NamStyle(0, 0, 0, 0, 0, 0, 0, capital, 2, 0);
            case STANDARD -> new NamStyle(1, 0, 1, 1, 1, 0, 0, capital, 1, 0);
        };
    }
}
