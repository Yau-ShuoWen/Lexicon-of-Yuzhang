package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Tool.dataStructure.UChar;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.data.domain.IPA.Phonogram;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.domain.Character.HanziShow;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.domain.Word.CiyuShow;
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
    @GetMapping ("{l}/{d}/query")
    public List<SearchResult> search(
            @PathVariable Dialect d, @PathVariable Language l, @RequestParam String query,
            @RequestParam boolean vague
    )
    {
        List<SearchResult> ans = new ArrayList<>();

        ans.addAll(h.getHanziSearchInfo(query, l, d, vague));
        ans.addAll(c.getCiyuSearchInfo(query, l, d, vague));

        return ans;
    }

    /**
     * 查询具体汉字信息
     */
    @GetMapping ("{l}/{d}/hanzi")
    public APIResponse<HanziShow> hanziSearch(
            @PathVariable Dialect d, @PathVariable Language l, @RequestParam UChar query,
            @RequestParam Phonogram phonogram,
            @RequestParam IPASyllableStyle syllableStyle,
            @RequestParam IPAToneStyle toneStyle
    )
    {
        try
        {
            return APIResponse.success(h.getHanziDetailInfo(
                    query, l, d, PinyinOption.of(phonogram, syllableStyle, toneStyle)));
        } catch (Exception e)
        {
            if (e instanceof NoSuchElementException)
                return APIResponse.failure(ScTcText.get("沒有查到漢字", l).toString());

            e.printStackTrace();
            return APIResponse.failure(ScTcText.get("查找漢字失敗", l).toString());
        }
    }

    @GetMapping ("{l}/{d}/ciyu")
    public APIResponse<CiyuShow> ciyuSearch(
            @PathVariable Dialect d, @PathVariable Language l, @RequestParam UString query,
            @RequestParam Phonogram phonogram,
            @RequestParam IPASyllableStyle syllableStyle,
            @RequestParam IPAToneStyle toneStyle
    )
    {
        try
        {
            return APIResponse.success(c.getCiyuDetailInfo(
                    query, l, d,
                    PinyinOption.of(phonogram, syllableStyle, toneStyle)
            ));
        } catch (Exception e)
        {
            if (e instanceof NoSuchElementException)
                return APIResponse.failure(ScTcText.get("沒有查到詞語", l).toString());

            e.printStackTrace();
            return APIResponse.failure(ScTcText.get("查找詞語失敗", l).toString());
        }
    }
}
