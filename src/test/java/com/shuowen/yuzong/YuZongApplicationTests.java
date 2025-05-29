package com.shuowen.yuzong;

import com.shuowen.yuzong.dao.model.PinyinIPA.NamIPA;
import com.shuowen.yuzong.service.impl.NamHZServiceImpl;
import com.shuowen.yuzong.service.impl.NamPYServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class YuZongApplicationTests
{
    @Autowired
    private NamHZServiceImpl namHZServiceImpl;

    @Autowired
    NamPYServiceImpl namPYService;

    @Test
    void contextLoads()
    {
        var a=namPYService.getAll();

        List<String> s=new ArrayList<>();
        int j=1;
        for (var i:a)
        {
            String now=i.getCode();

            NamIPA b=namPYService.constructCode(now);

            if(b.toString().contains("-")) continue;
            if(b.equals(i)) System.out.println(j++);
            else System.out.println(i+"\n"+b);
        }


    }

}
