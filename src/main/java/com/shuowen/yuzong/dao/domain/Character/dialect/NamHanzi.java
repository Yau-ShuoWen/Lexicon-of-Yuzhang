package com.shuowen.yuzong.dao.domain.Character.dialect;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Status;
import com.shuowen.yuzong.dao.domain.Character.Hanzi;
import com.shuowen.yuzong.dao.model.Character.CharEntity;

import java.util.*;
import java.util.function.Function;

public class NamHanzi extends Hanzi<NamPinyin, NamStyle>
{
    protected NamHanzi(CharEntity ch, NamStyle style, Status statue,
                       Function<Set<NamPinyin>, Map<NamPinyin, Map<String, String>>> ipaSE)
    {
        super(ch, style, statue, ipaSE);
    }

    public static NamHanzi of(CharEntity ch, NamStyle style, Status statue,
                              Function<Set<NamPinyin>, Map<NamPinyin, Map<String, String>>> ipaSE)
    {
        return new NamHanzi(ch, style, statue, ipaSE);
    }

    @Override
    protected NamPinyin pinyinOf(String str)
    {
        return NamPinyin.of(str);
    }

    @Override
    protected String formatting(String s,NamStyle style)
    {
        return NamPinyin.formatting(s, style);
    }

    @Override
    protected String dict()
    {
        return "ncdict";
    }
}
