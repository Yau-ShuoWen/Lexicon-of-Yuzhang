package com.shuowen.yuzong.Auth;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.controller.setting.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest
{
    @Autowired
    AuthController c;

    @Test
    void test()
    {
        if (ObjectTool.unchecked(true)) return;  // 开启测试请把true改成false

        c.createUser("", "");
    }
}
