package com.shuowen.yuzong.Linguistics.IPA;

import com.shuowen.yuzong.util.tuple.Maybe;

public interface IPinyin
{
    String getSyll();

    Maybe<Integer> getTone();

    Integer getCorner();

    String getCode();

    String toString();
}
