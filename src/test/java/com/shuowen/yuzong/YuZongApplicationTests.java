package com.shuowen.yuzong;

import com.shuowen.yuzong.dto.NamCharDetial;
import com.shuowen.yuzong.redis.RedisDao;
import com.shuowen.yuzong.service.Interface.StudentService;
import com.shuowen.yuzong.service.impl.MdrHZServiceImpl;
import com.shuowen.yuzong.service.impl.NamHZServiceImpl;
import com.shuowen.yuzong.service.impl.NamPYServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
class YuZongApplicationTests
{
    @Autowired
    private NamHZServiceImpl namHZServiceImpl;

    @Test
    void contextLoads()
    {
        var s=namHZServiceImpl.getMenu("捏");

        for (var n:s)
        {
            System.out.println(n);
        }
    }

}
