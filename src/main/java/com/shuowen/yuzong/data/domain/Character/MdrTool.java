package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Linguistics.Mandarin.Zhuyin;

public class MdrTool
{
    /**
     * 转换汉语拼音，带上汉字
     */
    public static String initWithPinyin(String ch)
    {
        // Read已经有括号了
        String[] tmp = ch.split(" ");
        return String.format("%s %s", tmp[0], HanPinyin.of(tmp[1]).getRead().toString());
    }

    /**
     * 转换汉语拼音，纯拼音
     */
    public static String showWithPinyin(String ch)
    {
        // Read已经有括号了
        return HanPinyin.of(ch.split(" ")[1]).getRead().toString();
    }

    /**
     * 对于注音符号没区别
     */
    public static String showWithZhuyin(String ch)
    {
        // 注音是不需要括号，所以直接返回
        return Zhuyin.of(ch.split(" ")[1]).toString();
    }
}
