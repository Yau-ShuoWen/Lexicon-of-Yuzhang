package com.shuowen.yuzong.service.impl.Word;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.UniqueList;
import com.shuowen.yuzong.Tool.JavaUtilExtend.WeightSort;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.format.ObfInt;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.domain.Word.CiyuItem;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.domain.Word.CiyuShow;
import com.shuowen.yuzong.data.mapper.Word.CiyuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CiyuService
{
    @Autowired
    CiyuMapper cy;

    private List<CiyuItem> getCiyu(String query, Language l, Dialect d, int grading)
    {
        var res = switch (grading)
        {
            case 1 -> cy.findCiyuByScTcInRange(query, d.toString());
            case 2 -> cy.findCiyuByVagueInRange(query, d.toString());
            default -> throw new RuntimeException("超范围");
        };
        return CiyuItem.listOf(res, l);
    }

    /**
     * 通过关键词，搜索出一系列搜索结果，但只保留基本信息
     */
    public List<SearchResult> getCiyuSearchInfo(String query, Language l, Dialect d, boolean vague)
    {
        // 排序说明：
        // 0. 主词语是指词条本身，模糊识别是指方便查询的内容，如（主：什哩；模糊：什么、甚莫、傻、犀利）
        // 1. （getSortKey）每一个词条先选举出和搜索词最像的词语
        // 2. （priority）这个词语作为键排序。但是作为惩罚/奖励，优先级是词条的主词汇拥有1.0的得分，模糊识别的词汇0.0

        // 去重
        UniqueList<CiyuItem, Integer> ulist = UniqueList.of(CiyuItem::getId);
        for (String hanzi : UString.of(query).chars())
            ulist.addAll(getCiyu(hanzi, l, d, vague ? 2 : 1));
        List<CiyuItem> list = ulist.getList();

        var priority = ListTool.mapping(list, (CiyuItem i) -> i.getSortKey(query).equals(i.getCiyu()) ? 1.0 : 0.0);
        WeightSort.sort(list, priority, (CiyuItem i) -> i.getSortKey(query).toString(), query, null);

        List<SearchResult> res = new ArrayList<>();
        for (var i : list)
        {
            var ans = new SearchResult();

            ans.setTitle(i.getCiyu() + "  " +
                    i.getPinyin(d).stream().map(RPinyin::toString).collect(Collectors.joining("")));

            String explain = "{b 【詞語】}  ";

            if (!i.getSortKey(query).equals(i.getCiyu())) explain += "根據{b 「%s」}模糊識別；";
            explain = StringTool.deleteBack(explain);
            explain = String.format(ScTcText.get(explain, l).toString(), i.getSortKey(query));

            ans.setExplain(explain);
            ans.setTag("ciyu");
            ans.setInfo(Map.of("query", ObfInt.encode(i.getId())));

            res.add(ans);
        }
        return res;
    }

    public CiyuShow getCiyuDetailInfo(Integer id, Language l, Dialect d, PinyinOption op)
    {
        return CiyuShow.of(
                CiyuItem.of(cy.findCiyuByWordId(id, d.toString()), l),
                new IPAData(l, d, op)
        );
    }
}
