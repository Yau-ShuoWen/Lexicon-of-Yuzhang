package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.data.model.Character.MdrChar;

import java.util.*;

public class MdrTool
{
    // 给编辑者看的
    public static String settle(String ch)
    {
        String[] tmp = ch.split(" ");
        ch = tmp[0] + " " + HanPinyin.topMark(tmp[1]);
        return ch;
    }

    // 给用户看的，所以要用括号括起来
    public static String format(String ch)
    {
        String[] tmp = ch.split(" ");
        ch = tmp[0] + " [" + HanPinyin.topMark(tmp[1]) + "]";
        return ch;
    }

    public static List<MdrChar> settle(List<MdrChar> ch)
    {
        for (var i : ch)
        {
            i.setInfo(settle(i.getInfo()));
        }
        return ch;
    }
}
