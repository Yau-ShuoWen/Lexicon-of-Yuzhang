package com.shuowen.yuzong.dao.domain.Character.dialect;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.dao.domain.Character.Hanzi;
import com.shuowen.yuzong.dao.model.Character.CharEntity;

public class NamHanzi extends Hanzi<NamPinyin,NamStyle>
{
    public NamHanzi(CharEntity ch, NamStyle style)
    {
        super(ch,style);
    }

    public static Hanzi of(CharEntity ch, NamStyle style)
    {
        return new NamHanzi(ch,style);
    }

    @Override
    protected void scan(NamStyle style)
    {
        stdPy =NamPinyin.formatting(stdPy,style);
        for (var i:mulPy.values())
            i.put("content",NamPinyin.formatting(i.get("content"),style));

        //TODO :ipaExp的扩展
        //for ()

    }
}
