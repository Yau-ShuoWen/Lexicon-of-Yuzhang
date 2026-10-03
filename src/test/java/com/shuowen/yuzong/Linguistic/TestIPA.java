package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.linguistics.pinyinv2.UniPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestIPA
{
    @Test
    void check()
    {
        UniPinyin pinyin = Dialect.LAC.trustedCreatePinyin("zi1");
        assertEquals("tsɿ_˦˨", pinyin.searchIPA(DictCode.NCDICT).getValue());
    }
}
