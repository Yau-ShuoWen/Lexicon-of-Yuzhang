package com.shuowen.yuzong.Linguistic;

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
        if (ObjectTool.unchecked(true)) return;

        for (var i : m.findAllPinyin("nam"))
        {
            NamPinyin n = NamPinyin.of(i.getStandard(), false);
            String a = n.getCode();
            if (!a.equals(i.getCode())) ObjectTool.print(i.getStandard(), i.getCode(), a);
        }
    }
}