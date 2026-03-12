package com.shuowen.yuzong.Tool.dataStructure.text;

import com.shuowen.yuzong.Tool.dataStructure.UString;

public interface UElement<T extends UElement<T>> extends Comparable<T>
{
    /**
     * 代码点长度
     */
    int length();

    /**
     * 对比单个字符
     */
    boolean contentEquals(char ch);

    /**
     * 对比任意字符序列
     */
    boolean contentEquals(CharSequence s);


    /**
     * 对比Unicode字符序列
     */
    boolean contentEquals(UElement<?> other);


    /**
     * 转字符串
     */
    String toString();

    /**
     * 转Unicode字符串
     */
    UString toUString();
}