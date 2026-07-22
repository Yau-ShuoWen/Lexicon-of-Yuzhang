package com.shuowen.yuzong.Linguistics.pinyin;

import com.shuowen.yuzong.Linguistics.Format.WUHStyle;
import com.shuowen.yuzong.Linguistics.Scheme.DPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.tuple.Maybe;

public class WUHPinyin extends UniPinyin<WUHStyle>
{
    protected WUHPinyin(SPinyin s)
    {
        super(s);
    }

    @Override
    public String initCode()
    {
        return "";
    }

    @Override
    public void checkToneValid()
    {

    }

    @Override
    public void checkEncodable()
    {

    }

    @Override
    public int initCorner()
    {
        return 0;
    }

    @Override
    public String initWeight()
    {
        return "";
    }

    @Override
    public RPinyin toRPinyin(WUHStyle params)
    {
        return null;
    }

    @Override
    public SPinyin toSPinyin(WUHStyle params)
    {
        return null;
    }

    @Override
    public DPinyin toDPinyin(WUHStyle params)
    {
        return null;
    }

    public static Maybe<WUHPinyin> tryOf(SPinyin s, boolean fromDatabase)
    {
        try
        {
            var p = fromDatabase ? s : s;//LACKeyboard.normalize(s);
            return Maybe.exist(new WUHPinyin(p));
        } catch (InvalidPinyinException e)
        {
            return Maybe.nothing();
        }
    }
}
