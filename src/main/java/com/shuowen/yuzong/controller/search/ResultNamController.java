package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Tool.dataStructure.Status;
import com.shuowen.yuzong.dao.domain.Character.HanziEntry;
import com.shuowen.yuzong.dao.domain.Character.dialect.NamHanzi;
import com.shuowen.yuzong.service.impl.Character.NamHanziServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/search/nam")
public class ResultNamController
{
    @Autowired
    NamHanziServiceImpl s;

    @GetMapping (value = "/byhanzi/certain")
    public HanziEntry<NamHanzi> precise(@RequestParam String hanzi, @RequestParam int code)
    {
        return s.getHanziScTc(hanzi, null, Status.of(code));
    }

    @GetMapping (value = "/byhanzi/certain", params = "lang")
    public List<HanziEntry<NamHanzi>> preciseGroup(
            @RequestParam String hanzi, @RequestParam String lang, @RequestParam int code)
    {
        return s.getHanziScTcGroup(hanzi, lang, null, Status.of(code));
    }

    @GetMapping (value = "/byhanzi/vague")
    public HanziEntry<NamHanzi> vague(@RequestParam String hanzi
            , @RequestParam int code)
    {
        return s.getHanziVague(hanzi, null, Status.of(code));
    }

    @GetMapping (value = "/byhanzi/vague", params = "lang")
    public List<HanziEntry<NamHanzi>> vagueGroup(
            @RequestParam String hanzi, @RequestParam String lang, @RequestParam int code)
    {
        return s.getHanziVagueGroup(hanzi, lang, null, Status.of(code));
    }
}
