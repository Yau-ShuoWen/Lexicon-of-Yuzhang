package com.shuowen.yuzong.ysw.controller;

import com.shuowen.yuzong.util.map.KV;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.ysw.data.mapper.PersonalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/personal/")
public class PersonalController
{
    @Autowired
    PersonalMapper m;

    @GetMapping ("/hello")
    public APIResponse<ScTcText> greeting()
    {
        return APIResponse.success(new ScTcText(KV.get("website-greeting:ysw")));
    }

//    @GetMapping ("/dict/search/{l}")
//    public List<Cipher> query(@PathVariable Language l, @RequestParam String q)
//    {
//        return Cipher.listOf(m.search(q), l);
//    }
}
