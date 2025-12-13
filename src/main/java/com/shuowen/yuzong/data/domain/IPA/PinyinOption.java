package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;

public class PinyinOption
{
    Triple<Phonogram, IPASyllableStyle, IPAToneStyle> data;

    public PinyinOption(Phonogram p, IPASyllableStyle s, IPAToneStyle t)
    {
        data = Triple.of(p, s, t);
    }

    public static PinyinOption of(Phonogram p, IPASyllableStyle s, IPAToneStyle t)
    {
        return new PinyinOption(p, s, t);
    }

    public static PinyinOption defaultOf()
    {
        return new PinyinOption(
                Phonogram.PinyinIPA,
                IPASyllableStyle.CHINESE_SPECIAL,
                IPAToneStyle.FIVE_DEGREE_LINE
        );
    }

    public Phonogram getPhonogram()
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
