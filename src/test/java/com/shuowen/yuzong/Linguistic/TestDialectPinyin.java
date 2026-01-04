package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.Tool.TestTool.Counter;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.data.mapper.IPA.IPAMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestDialectPinyin
{
    @Autowired
    IPAMapper m;

    /**
     * 检查数据库的编码和本地的流程是否对的上<br>
     * 托了这个逆天的天天修改的拼音类的福
     */
    @Test
    void test()
    {
        if (ObjectTool.unchecked(true)) return;  // 开启测试请把true改成false

        for (var d : Dialect.getList())
        {
            System.out.println("方言：" + d);
            Counter ans = new Counter();
            for (var i : m.getAllSyllable(d.toString()))
            {
                var maybe = d.tryCreatePinyin(i.getStandard());

                String a = maybe.isValid() ? maybe.getValue().getCode() : null;
                if (ans.check(i.getCode().equals(a)))
                    ObjectTool.print(i.getStandard(), i.getCode(), a);
            }
            ans.report();
        }
    }
}