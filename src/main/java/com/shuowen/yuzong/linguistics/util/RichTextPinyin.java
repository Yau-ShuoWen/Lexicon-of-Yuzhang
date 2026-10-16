package com.shuowen.yuzong.linguistics.util;

public interface RichTextPinyin
{
    String toOriginalString(); // 字符串是什么就是什么

    String toHandleString();   // 基础替换内容

    String toShowString();     // 展示字符串，需要加上 [%s]
}
