package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.dao.domain.IPA.Phonogram;
import com.shuowen.yuzong.dao.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.dao.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.dao.domain.Word.NamCiyu;
import com.shuowen.yuzong.dao.dto.Character.HanziShow;
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

    @PostMapping (value = "/byhanzi")
    public List<HanziShow> hanziSearch(
            @RequestParam String hanzi,
            @RequestParam String lang,
            @RequestParam (required = false, defaultValue = "1") int phonogram,
            @RequestParam (required = false, defaultValue = "false") boolean vague,
            @RequestParam (required = false, defaultValue = "1") int toneStyle,
            @RequestParam (required = false, defaultValue = "0") int syllableStyle,
            @RequestBody (required = false) NamStyle style
    )
    {
        return s.getHanziFormatted(hanzi, lang, vague, style, Phonogram.of(phonogram),
                IPAToneStyle.of(toneStyle), IPASyllableStyle.of(syllableStyle));
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
