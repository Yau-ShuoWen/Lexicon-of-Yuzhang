package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Linguistics.Mandarin.Zhuyin;
import com.shuowen.yuzong.data.model.Character.MdrChar;

import java.util.*;

public class MdrTool
{
    /**
     * 转换汉语拼音，直接转换
     */
    public static String initWithPinyin(String ch)
    {
        String[] tmp = ch.split(" ");
        return String.format("%s  %s", tmp[0], HanPinyin.of(tmp[1]).getSplit());
    }

    /**
     * 转换汉语拼音，富文本格式
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

    /**
     *
     */
    public static List<MdrChar> initWithPinyin(List<MdrChar> ch)
    {
        for (var i : ch) i.setInfo(initWithPinyin(i.getInfo()));
        return ch;
    }
}
