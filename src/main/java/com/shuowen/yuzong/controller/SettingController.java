package com.shuowen.yuzong.controller;

import com.shuowen.yuzong.Linguistics.Format.JyutStyle;
import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/setting")
public class SettingController
{
    @GetMapping ("/nam/pinyin")
    public NamStyle nam()
    {
        return new NamStyle();
    }

    @GetMapping("jyut/pinyin")
    public JyutStyle jyut(){return new JyutStyle();}
}
