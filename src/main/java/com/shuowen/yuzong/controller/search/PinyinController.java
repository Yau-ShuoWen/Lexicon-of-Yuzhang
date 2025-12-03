package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.service.impl.Pinyin.PinyinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/pinyin/")
public class PinyinController
{
    @Autowired
    PinyinService s;

    @GetMapping (value = "{dialect}/style/init")
    public PinyinStyle pinyin(@PathVariable String dialect)
    {
        return s.getStandardStyle(Dialect.of(dialect));
    }

    /**
     * 传入Nam格式，预览效果
     */
    @PostMapping ("{dialect}/preview")
    public Map<String, String> preview(
            @PathVariable String dialect,
            @RequestBody NamStyle style)
    {
        Map<String, String> response = new HashMap<>();

        response.put("message", s.getPreview(style, Dialect.of(dialect)));
        return response;
    }
}
