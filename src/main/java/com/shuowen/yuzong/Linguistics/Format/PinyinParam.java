package com.shuowen.yuzong.Linguistics.Format;

import com.shuowen.yuzong.Tool.dataStructure.option.Capital;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;

public class PinyinParam
{
    Capital capital;
    Scheme scheme;

    public PinyinParam(Capital capital, Scheme scheme)
    {
        this.capital = capital;
        this.scheme = scheme;
    }

    public static PinyinParam of(Capital capital, Scheme scheme)
    {
        return new PinyinParam(capital, scheme);
    }

    public PinyinParam(Scheme scheme)
    {
        this.capital = Capital.LOWER;
        this.scheme = scheme;
    }

    public static PinyinParam of(Scheme s)
    {
        return new PinyinParam(s);
    }

    public static PinyinParam[] defaultList()
    {
        return new PinyinParam[]{PinyinParam.of(Scheme.STANDARD), PinyinParam.of(Scheme.KEYBOARD)};
    }
}
