package com.shuowen.yuzong.service.impl.Word;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Tool.DataVersionCtrl.SetCompareUtil;
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
import com.shuowen.yuzong.data.domain.Word.CiyuCreate;
import com.shuowen.yuzong.data.domain.Word.CiyuItem;
import com.shuowen.yuzong.data.domain.Word.CiyuUpdate;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.domain.Word.CiyuShow;
import com.shuowen.yuzong.data.mapper.Word.CiyuMapper;
import com.shuowen.yuzong.data.model.Word.CiyuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional (rollbackFor = {Exception.class})
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

        var priority = ListTool.mapping(list, i -> Objects.equals(i.getSortKey(query), i.getCiyu()) ? 1.0 : 0.0);
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
            ans.setInfo(Map.of("query", i.getCiyu()));

            res.add(ans);
        }
        return res;
    }

    public CiyuShow getCiyuDetailInfo(UString query, Language l, Dialect d, PinyinOption op)
    {
        var entity = ListTool.checkSizeOne(cy.findCiyuByScOrTc(query.toString(), d.toString(), l.toString()),
                "没有找到词语", "查到了过多的数据");
        return CiyuShow.of(CiyuItem.of(entity, l), new IPAData(l, d, op));
    }

    /**
     * 在编辑界面的时候，给一个非常宽松的筛选
     */
    public List<SearchResult> getCiyuFilterInfo(String query, Dialect d)
    {
        // 唯一键是（编了码的id）
        UniqueList<SearchResult, String> ans =
                UniqueList.of(i -> i.getInfo().get("query").toString());

        for (String hanzi : UString.of(query).chars())
        {
            for (var i : cy.findCiyuByVagueInRange(hanzi, d.toString()))
            {
                var tmp = new SearchResult();

                // 相同显示一个："文" ，不同显示两个："车 / 車"
                tmp.setTitle(Objects.equals(i.getSc(), i.getTc()) ?
                        i.getSc() : i.getSc() + " / " + i.getTc());
                tmp.setExplain("");
                tmp.setTag("");
                tmp.setInfo(Map.of("query", ObfInt.encode(i.getId())));

                ans.add(tmp);
            }
        }
        return ans.getList();
    }

    /**
     * 编辑词条的时候的明确的词条
     */
    public CiyuUpdate getCiyuById(int id, Dialect d)
    {
        return new CiyuUpdate(
                cy.findCiyuByWordId(id, d.toString()).get(0),//TODO
                cy.findCiyuSimilarByWordId(id, d.toString())
        );
    }

    /**
     * 提交
     */
    @Transactional (rollbackFor = {Exception.class})
    public void editCiyu(CiyuUpdate ce, Dialect d)
    {
        var data = ce.checkAndTransfer(d);

        var wd = data.getLeft();

        // 通过唯一键寻找数据库里是否也有
        CiyuEntity maybe = cy.findByUniqueKey(wd, d.toString());

        // 如果没找到（maybe == null），说明是新增，可以插入
        // 如果id是同一个（id == id），那么说明是原地更新
        // 其他情况为新增但是唯一键冲突，那么说明是冲突，抛出异常
        if (maybe != null && !maybe.getId().equals(wd.getId()))
            throw new IllegalArgumentException("数据 简体：" + wd.getSc() + " 繁体：" + wd.getTc() + "重复");


        // 处理主表插入
        if ((wd.getId() == null || wd.getId() <= 0))
            cy.insertWord(wd, d.toString());
        else cy.updateWordById(wd, d.toString());

        int id = wd.getId();

        var sim = data.getRight();
        for (var i : sim) i.setWordId(id);
        for (var i : SetCompareUtil.compare(
                new HashSet<>(cy.findCiyuSimilarByWordId(id, d.toString())),
                new HashSet<>(sim)))
        {
            switch (i.getChangeType())
            {
                case ADDED -> cy.insertWordSimilar(i.getNewItem(), d.toString());
                case MODIFIED -> cy.updateWordSimilarById(i.getNewItem(), d.toString());
                case DELETED -> cy.deleteWordSimilarById(i.getOldItem().getId(), d.toString());
            }
        }
    }

    public void createCiyu(CiyuCreate ci, Dialect d)
    {
        cy.insertWord(ci.checkAndTransfer(d), d.toString());
    }
}
