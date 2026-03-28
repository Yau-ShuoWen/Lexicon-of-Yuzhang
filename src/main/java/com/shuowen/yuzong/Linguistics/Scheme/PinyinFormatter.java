package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;

/**
 * 方言拼音格式化类
 */
public class PinyinFormatter
{
    /**
     * 拼音直接返回对应的内容
     */
    public static RPinyin handle(UniPinyin<?> pinyin, Dialect d, PinyinParam param)
    {
        return pinyin.format(d.createStyle(param));
    }

    /**
     * 默认的初始化方式就是标准
     */
    public static RPinyin handle(UniPinyin<?> pinyin, Dialect d)
    {
        return handle(pinyin, d, PinyinParam.of(Scheme.STANDARD));
    }

    public static RPinyin handle(SPinyin pinyin, Dialect d)
    {
        return d.trustedCreatePinyin(pinyin).format(d.createStyle(PinyinParam.of(Scheme.STANDARD)));
    }

    /**
     * 对于专业的版本，直接使用对应的style处理
     */
    public static <U extends PinyinStyle> RPinyin handle(UniPinyin<U> pinyin, U style)
    {
        return pinyin.format(style);
    }
}