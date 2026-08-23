package com.shuowen.yuzong.ysw.controller;

import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.map.KV;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.ysw.data.domain.Cipher;
import com.shuowen.yuzong.ysw.data.mapper.PersonalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/personal/")
public class PersonalController
{
    @Autowired
    PersonalMapper m;

    @GetMapping ("/hello")
    public ScTcText greeting()
    {
        return new ScTcText(KV.get("website-greeting:ysw"));
    }

    @GetMapping ("/dict/search/{l}")
    public List<Cipher> query(@PathVariable Language l, @RequestParam String q)
    {
        return Cipher.listOf(m.search(q), l);
    }
}
