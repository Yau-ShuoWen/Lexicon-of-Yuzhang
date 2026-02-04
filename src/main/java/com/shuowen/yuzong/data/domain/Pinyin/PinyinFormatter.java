package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;

import java.util.*;

/**
 * 方言拼音格式化类
 */
public class PinyinFormatter
{
    /**
     * 拼音直接返回对应的内容
     */
    public static String handle(UniPinyin<?> pinyin, Dialect d, PinyinParam param)
    {
        return pinyin.toString(d.createStyle(param));
    }

    /**
     * 对某一个方言，指定一系列格式化方法的拼音格式化
     */
    public static String handle(UniPinyin<?> pinyin, Dialect d, PinyinParam[] param)
    {
        return String.join(" | ", ListTool.mapping(List.of(param), i -> handle(pinyin, d, i)));
    }

    /**
     * 默认的初始化方式就是两个并列的列出来
     */
    public static String handle(UniPinyin<?> pinyin, Dialect d)
    {
        return handle(pinyin, d, PinyinParam.defaultList());
    }

    /**
     * 对于专业的版本，直接使用对应的style处理
     */
    public static <U extends PinyinStyle> String handle(UniPinyin<U> pinyin, U style)
    {
        return pinyin.toString(style);
    }
}