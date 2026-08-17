package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.dict.service.IPA.IPAService;
import com.shuowen.yuzong.linguistics.util.SplitedPinyin;
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
            ipaService.insertSyllable(d.trustedCreatePinyin(SplitedPinyin.of(i)), d);
        }
    }
}
