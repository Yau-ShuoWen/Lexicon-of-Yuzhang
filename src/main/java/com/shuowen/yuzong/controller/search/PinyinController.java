package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
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
        return Dialect.of(dialect).getStyle();
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

    @GetMapping ("{dialect}/normalize")
    public Triple<Integer, String,String> normalizeCheck(
            @PathVariable String dialect,
            @RequestParam String pinyin)
    {
        return PinyinChecker.check(pinyin, Dialect.of(dialect));
    }
}
