package com.shuowen.yuzong.Linguistics.Scheme;

public interface Pinyin
{
    boolean isValid();

    String getPinyin();

    Integer getTone();

    String getCode();

    @Override
    String toString();

    char getFourCornerTone();

    int syllableLen();
}
