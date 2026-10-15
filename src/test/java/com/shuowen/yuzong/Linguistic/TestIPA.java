package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.linguistics.pinyinv2.UniPinyin;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAFormatter;
import com.shuowen.yuzong.util.core.Dialect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestIPA
{
    @Test
    void check()
    {
        UniPinyin pinyin = Dialect.LAC.trustedCreatePinyin("zi1");
        var ipa = pinyin.searchIPA(DictCode.NCDICT).getValue();
        assertEquals("tsɿ˦˨", ipa.value());
        assertEquals("[-tsɿ˦˨]", ipa.toString());
        assertEquals("ɕyia˦˨", IPAFormatter.mergeFiveDegree("ɕyia42"));
        assertEquals("lan˥˥˥", IPAFormatter.mergeFiveDegree("lan55"));
        assertEquals("·lan", IPAFormatter.mergeFiveDegree("lan0"));
    }
}
