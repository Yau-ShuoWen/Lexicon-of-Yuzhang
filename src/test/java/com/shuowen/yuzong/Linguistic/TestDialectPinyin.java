package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.Tool.TestTool.Counter;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.data.mapper.IPA.IPAMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestDialectPinyin
{
    @Autowired
    IPAMapper m;

    @Test
    void test()
    {
        if (ObjectTool.unchecked(true)) return;  // 这句话为了打包的时候跳过测试，开启测试请把true改成false

        Counter ans = new Counter();
        for (var i : m.findAllPinyin("nam"))
        {
            NamPinyin n = NamPinyin.of(i.getStandard());
            String a = n.getCode();
            if (ans.check(a.equals(i.getCode())))
                ObjectTool.print(i.getStandard(), i.getCode(), a);
        }
        ans.report();
    }
}