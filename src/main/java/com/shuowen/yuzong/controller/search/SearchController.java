package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.UChar;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.data.domain.IPA.PinyinMode;
import com.shuowen.yuzong.data.domain.Character.HanziShow;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinConfig;
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
    public List<List<SearchResult>> search(@PathVariable Dialect d, @PathVariable Language l, @RequestParam UString query)
    {
        if (query.contentEquals("random")) return List.of(c.getCiyuRandom(l, d));

        var hanzi = h.getHanziSearchInfo(query, l, d);
        var ciyu = c.getCiyuSearchInfo(query, l, d);

        List<List<SearchResult>> ans = new ArrayList<>();
        ans.add(ListTool.merge(hanzi, ciyu.getLeft()));
        ans.add(ciyu.getRight());

        return ans;
    }

    /**
     * 查询具体汉字信息
     */
    @GetMapping ("{l}/{d}/hanzi")
    public APIResponse<HanziShow> hanziSearch(
            @PathVariable Dialect d, @PathVariable Language l, @RequestParam UChar query,
            @RequestParam PinyinMode phonogram,
            @RequestParam IPASyllStyle syllableStyle,
            @RequestParam IPAToneStyle toneStyle
    )
    {
        try
        {
            System.out.println(phonogram);
            return APIResponse.success(h.getHanziDetailInfo(
                    query, l, d, new PinyinConfig(l, d, phonogram, syllableStyle, toneStyle)
            ));
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
            @RequestParam PinyinMode phonogram,
            @RequestParam IPASyllStyle syllableStyle,
            @RequestParam IPAToneStyle toneStyle
    )
    {
        try
        {
            System.out.println(phonogram);
            return APIResponse.success(c.getCiyuDetailInfo(
                    query, l, d,
                    new PinyinConfig(l, d, phonogram, syllableStyle, toneStyle)
            ));
        } catch (Exception e)
        {
            if (e instanceof NoSuchElementException)
                return APIResponse.failure(ScTcText.get("沒有查到詞語 not found", l).toString());

            e.printStackTrace();
            return APIResponse.failure(ScTcText.get("查找詞語失敗", l).toString());
        }
    }
}
