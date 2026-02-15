package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.service.impl.IPA.IPAService;
import com.shuowen.yuzong.service.impl.Reference.RefService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestFormat
{
    @Autowired
    IPAService ipa;

    @Autowired
    RefService refer;

    @Test
    void a()
    {
        if (ObjectTool.unchecked(true)) return;  // 开启测试请把true改成false

        String s = "我是一个南昌人，南昌的拼音是[lan4][cong1]，普通话拼音是[+nan2][+chang1]，国际音标是[*lan4][*cong1]，键盘写作[/lan4][/cong1]";
        System.out.println("写法为：" + s);
        System.out.println("效果为：" + RichTextUtil.format(s, Dialect.NAM,
                new IPAData(Language.SC, Dialect.NAM, PinyinOption.defaultOf(), refer::getDictionaryMap, ipa::getIPA)));
    }
}
