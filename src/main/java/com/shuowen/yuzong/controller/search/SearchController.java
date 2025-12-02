package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.IPA.Phonogram;
import com.shuowen.yuzong.data.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.data.domain.Word.NamCiyu;
import com.shuowen.yuzong.data.dto.Character.HanziShow;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.service.impl.Character.NamHanziServiceImpl;
import com.shuowen.yuzong.service.impl.Word.NamCiyuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping ("/api/search/nam")
public class SearchController
{
    @Autowired
    NamHanziServiceImpl s;

    @Autowired
    NamCiyuServiceImpl t;

    /**
     * 适用于搜索时候整合所有信息列成一个列表供选择，所以这里的结果集是可插拔的
     */
    @GetMapping (value = "/search-query")
    public List<SearchResult> search(
            @RequestParam String query,
            @RequestParam String lang,
            @RequestParam boolean vague
    )
    {
        List<SearchResult> ans = new ArrayList<>();

        ans.addAll(s.getHanziSearch(query, lang, vague));
        // 之后还可以加上其他的东西

        return ans;
    }

    /**
     * 查询汉字信息
     */
    @GetMapping (value = "/by-hanzi")
    public APIResponse<HanziShow> hanziSearch(
            @RequestParam String hanzi,
            @RequestParam String lang,
            @RequestParam (required = false, defaultValue = "1") int phonogram,
            @RequestParam (required = false, defaultValue = "1") int toneStyle,
            @RequestParam (required = false, defaultValue = "0") int syllableStyle
    )
    {
        var res = s.getHanzShow(hanzi, lang, Phonogram.of(phonogram),
                IPAToneStyle.of(toneStyle), IPASyllableStyle.of(syllableStyle));

        return (res == null) ?
                APIResponse.failure("not found 未找到该汉字") :  // 前端解析到这个是一个错误回复检查"not found"字符
                APIResponse.success(res);
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
