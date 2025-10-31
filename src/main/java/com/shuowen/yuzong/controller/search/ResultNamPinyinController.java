package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.service.impl.Pinyin.NamPinyinServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/pinyin/nam")
public class ResultNamPinyinController
{
    @Autowired
    NamPinyinServiceImpl s;

    @GetMapping (value = "/style/init")
    public NamStyle pinyin()
    {
        return new NamStyle();
    }

    /**
     * 传入Nam格式，预览效果
     */
    @PostMapping ("/preview")
    public Map<String, String> preview(@RequestBody NamStyle style)
    {
        Map<String, String> response = new HashMap<>();

        response.put("message", s.getPreview(style));
        return response;
    }
}
