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
import com.shuowen.yuzong.service.impl.Word.CiyuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping ("/api/search/")
public class SearchController
{
    @Autowired
    HanziService h;

    @Autowired
    CiyuService c;

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

        ans.addAll(h.getHanziSearchInfo(query, Language.of(lang), Dialect.of(dialect), vague));
        ans.addAll(c.getHanziSearchInfo(query, Language.of(lang), Dialect.of(dialect), vague));

        return ans;
    }

    /**
     * 查询具体汉字信息
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
        try
        {
            return APIResponse.success(h.getHanzDetailInfo(hanzi, Language.of(lang), Dialect.of(dialect),
                    Phonogram.of(phonogram), IPAToneStyle.of(toneStyle), IPASyllableStyle.of(syllableStyle))
            );
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure("not found 未找到该汉字，或者汉字不唯一");  // 前端解析到这个是一个错误回复检查"not found"字符
        }
    }

}
