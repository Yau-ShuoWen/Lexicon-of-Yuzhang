package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.service.impl.Pinyin.PinyinService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestIPA
{
    @Autowired
    PinyinService py;

    @Test
    void check()
    {
        if (ObjectTool.unchecked(true)) return;  // 这句话为了打包的时候跳过测试，开启测试请把true改成false

        System.out.println(py.check(Dialect.NAM));
    }

    @Test
    void update()
    {
        if (ObjectTool.unchecked(true)) return;  // 这句话为了打包的时候跳过测试，开启测试请把true改成false

        py.updateIPA(Dialect.NAM);
        System.out.println(py.check(Dialect.NAM));
    }
}
