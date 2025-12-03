package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.IPA.Phonogram;
import com.shuowen.yuzong.data.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.data.dto.Character.HanziShow;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.service.impl.Character.HanziService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping ("/api/search/")
public class SearchController
{
    @Autowired
    HanziService s;

//    @Autowired
//    NamCiyuServiceImpl t;

    /**
     * 适用于搜索时候整合所有信息列成一个列表供选择，所以这里的结果集是可插拔的
     */
    @GetMapping (value = "{dialect}/search-query")
    public List<SearchResult> search(
            @PathVariable final String dialect,
            @RequestParam String query,
            @RequestParam String lang,
            @RequestParam boolean vague
    )
    {
        List<SearchResult> ans = new ArrayList<>();

        ans.addAll(s.getHanziSearchInfo(query, Language.of(lang), Dialect.of(dialect), vague));
        // 之后还可以加上其他的东西

        return ans;
    }

    /**
     * 查询汉字信息
     */
    @GetMapping (value = "{dialect}/by-hanzi")
    public APIResponse<HanziShow> hanziSearch(
            @PathVariable final String dialect,
            @RequestParam String hanzi,
            @RequestParam String lang,
            @RequestParam (required = false, defaultValue = "1") int phonogram,
            @RequestParam (required = false, defaultValue = "1") int toneStyle,
            @RequestParam (required = false, defaultValue = "0") int syllableStyle
    )
    {
        var res = s.getHanzDetailInfo(hanzi, Language.of(lang), Dialect.of(dialect), Phonogram.of(phonogram),
                IPAToneStyle.of(toneStyle), IPASyllableStyle.of(syllableStyle));

        return (res == null) ?
                APIResponse.failure("not found 未找到该汉字") :  // 前端解析到这个是一个错误回复检查"not found"字符
                APIResponse.success(res);
    }

//    @GetMapping (value = "/byciyu/certain")
//    public List<NamCiyu> ciyuPrecise(@RequestParam String ciyu)
//    {
//        return t.getCiyuByScTc(ciyu);
//    }
//
//    @GetMapping (value = "/byciyu/vague")
//    public List<NamCiyu> ciyuVague(@RequestParam String ciyu)
//    {
//        return t.getCiyuVague(ciyu);
//    }
//
//    @GetMapping (value = "/byciyu")
//    public List<NamCiyu> ciyuSentence(@RequestParam String sentence)
//    {
//        return t.getCiyuBySentence(sentence);
//    }
}
