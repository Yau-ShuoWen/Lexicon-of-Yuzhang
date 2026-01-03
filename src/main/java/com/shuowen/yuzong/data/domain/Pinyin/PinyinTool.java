package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;

import java.util.*;

/**
 * 静态拼音拼音处理类
 *
 * @implNote 因为内部有完善的机制处理null等情况，所以这个工具类本身不需要检查
 */
public class PinyinTool
{
    private static final String error = "无效拼音";

    /**
     * 拼音直接返回对应的内容
     */
    public static String formatPinyin(UniPinyin<?> pinyin, Dialect d, PinyinParam param)
    {
        return pinyin.toString(d.createStyle(param));
    }

    /**
     * 对某一个方言，指定一系列格式化方法的拼音格式化
     */
    public static String formatPinyin(UniPinyin<?> pinyin, Dialect d, PinyinParam[] param)
    {
        return String.join(" | ", ListTool.mapping(List.of(param), i -> formatPinyin(pinyin, d, i)));
    }

    public static String formatPinyin(UniPinyin<?> pinyin, Dialect d)
    {
        return formatPinyin(pinyin, d, PinyinParam.defaultList());
    }

    /**
     * 对某一个方言，指定某种格式化方法的拼音格式化
     *
     * @param py 字符串形式的拼音
     */
    public static String formatPinyin(String py, Dialect d, PinyinParam param)
    {
        var maybe = d.tryCreatePinyin(py);
        return (maybe.isValid()) ? formatPinyin(maybe.getValue(), d, param) : error;
    }


    public static String formatPinyin(String py, Dialect d, PinyinParam[] param)
    {
        var maybe = d.tryCreatePinyin(py);
        return (maybe.isValid()) ? formatPinyin(maybe.getValue(), d, param) : error;
    }

    public static String formatPinyin(String py, Dialect d)
    {
        var maybe = d.tryCreatePinyin(py);
        return (maybe.isValid()) ? formatPinyin(maybe.getValue(), d) : error;
    }

    public static <U extends PinyinStyle>
    String formatPinyin(String py, U style, Dialect d)
    {
        var maybe = d.tryCreatePinyin(py);
        return (maybe.isValid()) ? maybe.getValue().toString(style) : error;
    }
}