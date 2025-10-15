package com.shuowen.yuzong.dao.domain.Word;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.dao.model.Word.WordEntity;

import java.util.ArrayList;
import java.util.List;

public class NamCiyu extends Ciyu<NamPinyin, NamStyle>
{
    public NamCiyu(WordEntity wd)
    {
        super(wd);
    }

    public static NamCiyu of(WordEntity wd)
    {
        return new NamCiyu(wd);
    }

    public static List<NamCiyu> Listof(List<WordEntity> wd)
    {
        List<NamCiyu> list = new ArrayList<>();
        for (var i : wd) list.add(new NamCiyu(i));
        return list;
    }

    @Override
    protected NamPinyin pinyinOf(String str)
    {
        return NamPinyin.of(str);
    }

    @Override
    protected String formatting(String s, NamStyle style)
    {
        return NamPinyin.formatting(s, style);
    }

    @Override
    protected String dict()
    {
        return "ncdict";
    }
}
