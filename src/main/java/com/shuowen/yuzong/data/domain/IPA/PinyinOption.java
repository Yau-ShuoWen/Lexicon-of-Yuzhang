package com.shuowen.yuzong.data.domain.IPA;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;

public class PinyinOption
{
    Triple<Phonogram, IPASyllableStyle, IPAToneStyle> t;

    public PinyinOption(int phonogram, int syllable, int tone)
    {
        t = Triple.of(
                Phonogram.of(phonogram),
                IPASyllableStyle.of(syllable),
                IPAToneStyle.of(tone)
        );
    }

    public static PinyinOption of(int phonogram, int syllable, int tone)
    {
        return new PinyinOption(phonogram, syllable, tone);
    }

    public Phonogram getPhonogram()
    {
        return t.getLeft();
    }

    public IPASyllableStyle getSyllable()
    {
        return t.getMiddle();
    }

    public IPAToneStyle getTone()
    {
        return t.getRight();
    }

}
