package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.service.impl.IPA.IPAService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestInsertPinyin
{
    @Autowired
    IPAService ipaService;

    @Test
    void contextLoads()
    {
     //   if (ObjectTool.unchecked(true)) return;

        Dialect d = Dialect.LAC;
        String[] py = {"uok"};

        for (var i : py)
        {
            ipaService.insertSyllable(d.trustedCreatePinyin(SPinyin.of(i)), d);
        }
    }
}
