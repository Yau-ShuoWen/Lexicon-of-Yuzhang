package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.service.impl.Reference.RefTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestRefItem
{
    @Autowired
    RefTestService ref;

    @Test
    void t1()
    {
        if (ObjectTool.unchecked(true)) return;

        ref.checkPinyin(DictCode.of("ncdict"), new IPAData(Language.TC, Dialect.LAC, PinyinOption.defaultOf()));
    }
}
