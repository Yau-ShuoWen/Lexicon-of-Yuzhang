package com.shuowen.yuzong;

import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.TestTool.Counter;
import com.shuowen.yuzong.Tool.dataStructure.Range;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.controller.search.SearchController;
import com.shuowen.yuzong.data.mapper.IPA.IPAMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SpringBootTest
public class TestRandomSearch
{
    @Autowired
    SearchController se;

    /**
     * 检查数据库的编码和本地的流程是否对的上<br>
     * 托了这个逆天的天天修改的拼音类的福
     */
    @Test
    void test()
    {
        if (ObjectTool.unchecked(true)) return;  // 开启测试请把true改成false

        Map<Object, Integer> map = new HashMap<>();

        int times=500;
        for (var i: Range.of(times))
        {
            System.out.println(i+1);
            var str=se.search(Dialect.LAC, Language.SC, UString.of("random")).get(0).get(0).getTitle();
            if (map.containsKey(str)) map.put(str, map.get(str) + 1);
            else map.put(str, 1);
        }

        System.out.println("随机次数："+times);
        System.out.println("出现内容："+map.size());
        map.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(e -> System.out.println(e.getKey() + " : " + e.getValue()));
    }
}