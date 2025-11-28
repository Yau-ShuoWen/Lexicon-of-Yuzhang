package com.shuowen.yuzong.data.domain.IPA;

public enum Phonogram
{
    AllPinyin(1),
    PinyinIPA(2),
    AllIPA(3);

    //TODO：既要还要

    private int code;

    Phonogram(int code)
    {
        this.code = code;
    }

    /**
     * @param code 1 全部拼音 2 实用部分用拼音，专业资料用国际音标 3.全部国际音标
     */
    public static Phonogram of(int code)
    {
        switch (code)
        {
            case 1:
                return AllPinyin;
            case 2:
                return PinyinIPA;
            case 3:
                return AllIPA;
        }
        return AllPinyin;
    }
}
