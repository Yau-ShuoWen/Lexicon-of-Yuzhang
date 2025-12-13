package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.data.domain.IPA.Phonogram;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.dto.Character.HanziShow;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.dto.Word.CiyuShow;
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
        ans.addAll(c.getCiyuSearchInfo(query, Language.of(lang), Dialect.of(dialect), vague));

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
            @RequestParam (required = false, defaultValue = "0") int toneStyle,
            @RequestParam (required = false, defaultValue = "0") int syllableStyle
    )
    {
        try
        {
            return APIResponse.success(h.getHanziDetailInfo(
                    hanzi,
                    Language.of(lang),
                    Dialect.of(dialect),
                    PinyinOption.of(Phonogram.of(phonogram),
                            IPASyllableStyle.of(syllableStyle),
                            IPAToneStyle.of(toneStyle))
            ));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.toString());
            // 前端解析："not unique" 就是不唯一错误，"not found" 就是未找到错误
        }
    }

    @GetMapping (value = "{dialect}/by-ciyu")
    public APIResponse<CiyuShow> ciyuSearch(
            @PathVariable final String dialect,
            @RequestParam String ciyu,
            @RequestParam String lang,
            @RequestParam (required = false, defaultValue = "1") int phonogram,
            @RequestParam (required = false, defaultValue = "1") int toneStyle,
            @RequestParam (required = false, defaultValue = "0") int syllableStyle
    )
    {
        try
        {
            return APIResponse.success(c.getCiyuDetailInfo(
                    ciyu,
                    Language.of(lang),
                    Dialect.of(dialect),
                    PinyinOption.of(Phonogram.of(phonogram),
                            IPASyllableStyle.of(syllableStyle),
                            IPAToneStyle.of(toneStyle))
            ));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.toString());
        }
    }
}
