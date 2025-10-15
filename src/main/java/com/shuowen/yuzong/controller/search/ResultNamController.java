package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Tool.dataStructure.Status;
import com.shuowen.yuzong.dao.domain.Character.HanziEntry;
import com.shuowen.yuzong.dao.domain.Character.dialect.NamHanzi;
import com.shuowen.yuzong.dao.domain.Word.NamCiyu;
import com.shuowen.yuzong.dao.dto.HanziShow;
import com.shuowen.yuzong.service.impl.Character.NamHanziServiceImpl;
import com.shuowen.yuzong.service.impl.Word.NamCiyuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/search/nam")
public class ResultNamController
{
    @Autowired
    NamHanziServiceImpl s;

    @Autowired
    NamCiyuServiceImpl t;

    @GetMapping (value = "/byhanzi/certain")
    public HanziEntry<NamHanzi> hanziPrecise(@RequestParam String hanzi, @RequestParam int code)
    {
        return s.getHanziScTc(hanzi, null, Status.of(code));
    }

    @GetMapping (value = "/byhanzi/certain", params = "lang")
    public List<HanziEntry<NamHanzi>> hanziPreciseGroup(
            @RequestParam String hanzi, @RequestParam String lang, @RequestParam int code)
    {
        return s.getHanziScTcGroup(hanzi, null, Status.of(code), lang);
    }

    @GetMapping (value = "/byhanzi/certain/ask", params = "lang")
    public List<HanziShow> hanziPreciseAsk(
            @RequestParam String hanzi, @RequestParam String lang, @RequestParam int code)
    {
        return s.getHanziScTcOrganize(hanzi, null, Status.of(code), lang);
    }


    @GetMapping (value = "/byhanzi/vague")
    public HanziEntry<NamHanzi> hanziVague(@RequestParam String hanzi
            , @RequestParam int code)
    {
        return s.getHanziVague(hanzi, null, Status.of(code));
    }

    @GetMapping (value = "/byhanzi/vague", params = "lang")
    public List<HanziEntry<NamHanzi>> hanziVagueGroup(
            @RequestParam String hanzi, @RequestParam String lang, @RequestParam int code)
    {
        return s.getHanziVagueGroup(hanzi, null, Status.of(code), lang);
    }


    @GetMapping (value = "/byhanzi/vague/ask", params = "lang")
    public List<HanziShow> hanziVagueAsk(
            @RequestParam String hanzi, @RequestParam String lang, @RequestParam int code)
    {
        return s.getHanziVagueOrganize(hanzi, null, Status.of(code), lang);
    }


    @GetMapping (value = "/byciyu/certain")
    public List<NamCiyu> ciyuPrecise(@RequestParam String ciyu)
    {
        return t.getCiyuByScTc(ciyu);
    }

    @GetMapping (value = "/byciyu/vague")
    public List<NamCiyu> ciyuVague(@RequestParam String ciyu)
    {
        return t.getCiyuVague(ciyu);
    }

    @GetMapping (value = "/byciyu")
    public List<NamCiyu> ciyuSentence(@RequestParam String sentence)
    {
        return t.getCiyuBySentence(sentence);
    }
}
