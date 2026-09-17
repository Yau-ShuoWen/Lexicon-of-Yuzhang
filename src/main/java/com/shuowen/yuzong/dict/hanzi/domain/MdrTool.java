package com.shuowen.yuzong.dict.hanzi.domain;

import com.shuowen.yuzong.linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.linguistics.Mandarin.Zhuyin;

public class MdrTool
{
    private static String[] split(String info)
    {
        return info.split(" ", 2);
    }

    public static String getHanzi(String info)
    {
        return split(info)[0];
    }

    /** 不含漢字的標準普通話讀音，可用作簡繁記錄的去重鍵。 */
    public static String getPinyinKey(String info)
    {
        return split(info)[1];
    }

    /**
     * 转换汉语拼音，带上汉字
     */
    public static String initWithPinyin(String ch)
    {
        // Read已经有括号了
        String[] tmp = split(ch);
        return String.format("%s %s", tmp[0], HanPinyin.of(tmp[1]).getRead().toString());
    }

    /**
     * 转换汉语拼音，纯拼音
     */
    public static String showWithPinyin(String ch)
    {
        // Read已经有括号了
        return HanPinyin.of(getPinyinKey(ch)).getRead().toString();
    }

    /**
     * 对于注音符号没区别
     */
    public static String showWithZhuyin(String ch)
    {
        // 注音是不需要括号，所以直接返回
        return Zhuyin.of(getPinyinKey(ch)).toString();
    }
}
