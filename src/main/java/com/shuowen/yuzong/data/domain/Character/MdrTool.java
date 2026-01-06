package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Linguistics.Mandarin.Zhuyin;
import com.shuowen.yuzong.data.model.Character.MdrChar;

import java.util.*;

public class MdrTool
{
    /**
     * 直接转换
     */
    public static String initWithPinyin(String ch)
    {
        String[] tmp = ch.split(" ");
        return tmp[0] + " " + HanPinyin.topMark(tmp[1]);
    }

    /**
     * 直接转换
     */
    public static String initWithZhuyin(String ch)
    {
        String[] tmp = ch.split(" ");
        return tmp[0] + " " + Zhuyin.of(tmp[1]).toString();
    }

    /**
     * 富文本格式
     */
    public static String showWithPinyin(String ch)
    {
        String[] tmp = ch.split(" ");
        return tmp[0] + " [" + HanPinyin.topMark(tmp[1]) + "]";
    }


    /**
     * 富文本格式
     */
    public static String showWithZhuyin(String ch)
    {
        // 注音是不需要括号，所以直接返回
        return initWithZhuyin(ch);
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
