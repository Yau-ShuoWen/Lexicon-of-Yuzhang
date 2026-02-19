package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;

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


    /**
     * 尝试将一个字符串拆成声母和声调，如果没有音调补0，所有拼音都可以通用
     */
    //TODO 南宁话：谁说汉语音调小于十个的？回答我！
    public static Pair<String, Integer> trySplit(String text)
    {
        StringTool.checkTrimValid(text); // 如果是空的，取最后一个会报错

        char ch = StringTool.back(text);
        return NumberTool.closeBetween(ch, '0', '9') ?
                Pair.of(StringTool.deleteBack(text), ch - '0') :
                Pair.of(text, 0);
    }
}