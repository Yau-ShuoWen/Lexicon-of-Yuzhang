package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;

public class PinyinOption
{
    private final Triple<PinyinMode, IPASyllableStyle, IPAToneStyle> data;

    public PinyinOption(PinyinMode p, IPASyllableStyle s, IPAToneStyle t)
    {
        data = Triple.of(p, s, t);
    }

    public static PinyinOption of(PinyinMode p, IPASyllableStyle s, IPAToneStyle t)
    {
        return new PinyinOption(p, s, t);
    }

    public static PinyinOption defaultOf()
    {
        return new PinyinOption(
                PinyinMode.PROFESSIONAL,
                IPASyllableStyle.CHINESE_SPECIAL,
                IPAToneStyle.FIVE_DEGREE_LINE
        );
    }

    public PinyinMode getPinyinMode()
    {
        return data.getLeft();
    }

    public IPASyllableStyle getSyllable()
    {
        return data.getMiddle();
    }

    public IPAToneStyle getTone()
    {
        return data.getRight();
    }
}
