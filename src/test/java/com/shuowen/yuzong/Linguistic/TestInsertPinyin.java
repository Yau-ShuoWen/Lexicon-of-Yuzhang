package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.linguistics.pinyinv2.DictCode;
import com.shuowen.yuzong.linguistics.pinyinv2.UniPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestInsertPinyin
{
    @Test
    void unsupportedIpaIsEmpty()
    {
        UniPinyin pinyin = Dialect.LAC.trustedCreatePinyin("zi1");
        assertTrue(pinyin.searchIPA(DictCode.NCDIALSTD).isEmpty());
    }
}
