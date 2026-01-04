package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.service.impl.IPA.IPAService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestIPA
{
    @Autowired
    IPAService ipa;

    @Test
    void check()
    {
        if (ObjectTool.unchecked(true)) return;  // 开启测试请把true改成false

        System.out.println(ipa.checkIPA(Dialect.NAM).toString());
    }

    @Test
    void update()
    {
        if (ObjectTool.unchecked(true)) return;  // 开启测试请把true改成false

        ipa.updateIPA(Dialect.NAM);
        System.out.println(ipa.checkIPA(Dialect.NAM));
    }
}
