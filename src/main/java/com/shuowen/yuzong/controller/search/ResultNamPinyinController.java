package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.dao.domain.IPA.Yinjie;
import com.shuowen.yuzong.service.impl.pinyin.NamPinyinServiceImpl;
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

    @GetMapping (value = "/ipa", params = "dict")
    public String hhh(@RequestParam String pinyin, @RequestParam String dict)
    {
        return s.getIPA(new NamPinyin(pinyin), dict);
    }

    @GetMapping (value = "/ipa")
    public Map<String, String> hhh2(@RequestParam String pinyin)
    {
        return s.getAllIPA(new NamPinyin(pinyin));
    }

    /**
     * @return 如果返回null，都是无效拼音
     * */
    @GetMapping (value = "/construct")
    public Yinjie hhh3(@RequestParam String pinyin)
    {
        return s.constructSyllable(NamPinyin.of(pinyin));
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
