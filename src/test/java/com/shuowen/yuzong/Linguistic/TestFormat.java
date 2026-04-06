package com.shuowen.yuzong.Linguistic;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestFormat
{
    @Test
    void test()
    {
        //if (ObjectTool.unchecked(true)) return;  // 开启测试请把true改成false

        var s = UString.of("""
                我是一个南昌人, 南昌！
                
                方言拼音：
                方言拼音是[lan4][cong1] 或者 可写作[lan4 cong1]
                错误写法是[laan4] [conng1] 或者 可写作 [laaaan4] [conng1]
                被提示写法[yit6]
                
                普通话拼音是[+nan2][+chang1]，或者写作[+nan2 chang1]
                国际音标是[*lan4][*cong1]，或者写作[*lan4 cong1]
                键盘写作[/lan4][/cong1]，或者写作[/lan4 cong1]
                这是一个很生僻的需要手动构造的国际音标[][]
                
                ↑刚才是一个手动的空行，应该保留
                ↓下面的两种注释，不应该空行
                /*希望没有人看到这一段注释，当然这一行都不存在*/
                /*
                多行注释
                */
                这是一个需要{n 加粗}的内容，留给前端
                这是一个涉及到{l 南昌}词条的链接，但是还没有做，应该无事发生
                """);

        var data = new IPAData(Language.SC, Dialect.NAM, PinyinOption.defaultOf());

        System.out.println(RichTextUtil.format(s, data, true));
    }
}
