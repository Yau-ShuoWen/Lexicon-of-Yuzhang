package com.shuowen.yuzong.Linguistics.Scheme;

public interface Pinyin
{
    String getPinyin();

    Integer getTone();

    Integer getCorner();

    String getCode();

    String toString();

    int getInitialLen();
}
