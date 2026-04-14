package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Tool.dataStructure.Maybe;

public interface Pinyin
{
    String getSyll();

    Maybe<Integer> getTone();

    Boolean haveTone();

    Integer getToneDirectly();

    Integer getCorner();

    String getCode();

    String toString();
}
