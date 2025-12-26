package com.shuowen.yuzong.service.impl.Word;

import com.shuowen.yuzong.Tool.JavaUtilExtend.UniqueList;
import com.shuowen.yuzong.Tool.JavaUtilExtend.WeightSort;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.domain.Word.Ciyu;
import com.shuowen.yuzong.data.domain.Word.CiyuEntry;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.dto.Word.CiyuShow;
import com.shuowen.yuzong.data.mapper.Word.WordMapper;
import com.shuowen.yuzong.data.model.Word.WordEntity;
import com.shuowen.yuzong.service.impl.Pinyin.PinyinService;
import com.shuowen.yuzong.service.impl.Refer.ReferServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CiyuService
{
    @Autowired
    WordMapper cy;

    @Autowired
    private PinyinService ipa;

    @Autowired
    private ReferServiceImpl refer;

    /**
     * 查询匹配确定的简体或繁体，获得结果集
     */
    private List<WordEntity> getCiyuScOrTc(String word, Language lang, Dialect d)
    {
        return cy.findCiyuByScOrTc(word, lang.toString(), d.toString());
    }

    /**
     * 查询匹配简体或繁体中带有对应汉字的内容，获得结果集
     *
     * @param query 一个汉字
     */
    private List<WordEntity> getCiyuRangeScTc(String query, Dialect d)
    {
        return cy.findCiyuByVagueInRange(query, d.toString());
    }

    /**
     * 查询匹配简体、繁体、模糊识别中带有对应汉字的内容，获得结果集
     *
     * @param query 一个汉字
     */
    private List<WordEntity> getCiyuRangeVague(String query, Dialect d)
    {
        return cy.findCiyuByVagueInRange(query, d.toString());
    }

    /**
     * 获得结果的集根据分类结果合并内容
     *
     * @param grading 模糊识别粒度 1：{@code getHanziScOrTc}  2：{@code getHanziScTc}  3：{@code getHanziVague}
     */
    private List<CiyuShow> getCiyuOrganize(String query, Language lang, Dialect d, int grading)
    {
        CiyuEntry ans = CiyuEntry.of(switch (grading)
        {
            case 1 -> getCiyuScOrTc(query, lang, d);
            case 2 -> getCiyuRangeScTc(query, d);
            case 3 -> getCiyuRangeVague(query, d);
            default -> throw new RuntimeException("超范围");
        }, lang);
        return CiyuShow.listOf(ans, query);
    }

    public List<SearchResult> getCiyuSearchInfo(String query, Language lang, Dialect d, boolean vague)
    {
        UniqueList<CiyuShow, String> list = UniqueList.of(CiyuShow::getCiyu);
        for (String hanzi : UString.of(query))
        {
            var tmp = getCiyuOrganize(hanzi, lang, d, vague ? 3 : 2);
            WeightSort.sort(tmp, CiyuShow.getPrioriyList(tmp), CiyuShow::getKey, query, null);
            list.addAll(tmp);
        }

        List<SearchResult> res = new ArrayList<>();
        for (var i : list.getList())
        {
            res.add(new SearchResult(i.getCiyu(), "无", "ciyu",
                    Map.of("ciyu", i.getCiyu(), "lang", i.getLanguage())
            ));
        }
        return res;
    }

    public CiyuShow getCiyuDetailInfo(String ciyu, Language lang, Dialect d, PinyinOption op)
    {
        var ans = getCiyuOrganize(ciyu, lang, d,1);
        if (ans.size() != 1)
            throw new RuntimeException(ans.size() > 1 ? "not unique 词语不唯一" : "not found 未找到词语");

        ans.get(0).init(d.getStyle(), op, d, refer.getDictMap(d, lang), ipa::getMultiLine);
        return ans.get(0);
    }
}
