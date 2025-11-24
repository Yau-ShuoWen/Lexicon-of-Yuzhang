package com.shuowen.yuzong.dao.domain.Character;

import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.dao.model.Character.CharMdr;

import java.util.*;

public class MdrTool
{
    public static List<CharMdr> settle(List<CharMdr> ch)
    {
        for (var i : ch)
        {
            String[] tmp=i.getInfo().split(" ");
            i.setInfo(tmp[0] + " " + HanPinyin.topMark(tmp[1]));
        }
        return ch;
    }
}
