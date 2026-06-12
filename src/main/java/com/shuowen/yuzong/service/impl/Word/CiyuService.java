package com.shuowen.yuzong.service.impl.Word;

import com.shuowen.yuzong.Tool.DataVersionCtrl.SetCompareUtil;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.UniqueList;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.Tool.format.ObfInt;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.domain.Word.CiyuCreate;
import com.shuowen.yuzong.data.domain.Word.CiyuItem;
import com.shuowen.yuzong.data.domain.Word.CiyuUpdate;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.domain.Word.CiyuShow;
import com.shuowen.yuzong.data.mapper.LogMapper;
import com.shuowen.yuzong.data.mapper.Word.CiyuMapper;
import com.shuowen.yuzong.data.model.Word.CiyuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional (rollbackFor = {Exception.class})
public class CiyuService
{
    @Autowired
    private CiyuMapper cy;

    @Autowired
    private LogMapper log;

    /**
     * 通过关键词，搜索出一系列搜索结果，但只保留基本信息
     */
    public Twin<List<SearchResult>> getCiyuSearchInfo(UString query, Language l, Dialect d)
    {
        // 去重
        UniqueList<CiyuItem, Integer> ulist = UniqueList.of(CiyuItem::getId);
        for (String hanzi : query.chars())
            ulist.addAll(CiyuItem.listOf(cy.findCiyuByVagueInRange(hanzi, d.toString()), l));

        List<CiyuItem> all = ulist.getList();
        all.sort(Comparator.comparingDouble((CiyuItem i) -> i.score(query)).reversed());

        Twin<List<CiyuItem>> answer = Twin.of(new ArrayList<>(), new ArrayList<>());

        for (CiyuItem item : all)
        {
            if (item.match(query)) answer.getLeft().add(item);
            else answer.getRight().add(item);
        }

        return answer.map(i -> ListTool.mapping(i,
                item ->
                {
                    var ans = new SearchResult();
                    ans.setTitle(item.getCiyu() +
                            (item.vague(query) ? String.format("（%s）", item.getSortKey(query)) : "")
                    );
                    ans.setExplain(item.getPinyin(d));
                    ans.setSpecial(item.getSpecial() != 0);
                    ans.setTag("ciyu");
                    ans.setInfo(Map.of("query", item.getCiyu()));
                    return ans;
                }
        ));
    }

    public CiyuShow getCiyuDetailInfo(UString query, Language l, Dialect d, PinyinOption op)
    {
        var entity = ListTool.checkSizeOne(cy.findCiyuByScOrTc(query.toString(), d.toString(), l.toString()),
                "没有找到词语", "查到了过多的数据");
        return CiyuShow.of(CiyuItem.of(entity, l), new IPAData(l, d, op));
    }

    public List<SearchResult> getCiyuRandom(Language l, Dialect d)
    {
        var answer = CiyuItem.listOf(cy.findCiyuByWordId(cy.getSpecialWordIdByRandom(d.toString()), d.toString()), l);

        return ListTool.mapping(answer,
                item ->
                {
                    var ans = new SearchResult();
                    ans.setTitle(item.getCiyu());
                    ans.setExplain(item.getPinyin(d));
                    ans.setSpecial(item.getSpecial() != 0);
                    ans.setTag("ciyu");
                    ans.setInfo(Map.of("query", item.getCiyu()));
                    return ans;
                }
        );
    }

    /**
     * 在编辑界面的时候，给一个非常宽松的筛选
     */
    public List<SearchResult> getCiyuFilterInfo(UString query, Dialect d)
    {
        // 唯一键是id
        UniqueList<CiyuEntity, Integer> ulist = UniqueList.of(CiyuEntity::getId);
        for (String hanzi : query.chars()) ulist.addAll(cy.findCiyuByVagueInRange(hanzi, d.toString()));

        List<CiyuEntity> all = ulist.getList();
        all.sort(Comparator.comparingDouble((CiyuEntity i) -> CiyuItem.of(i, Language.TC).score(query)).reversed());

        return ListTool.mapping(all, i ->
                {
                    var ans = new SearchResult();
                    ans.setTitle(Objects.equals(i.getSc(), i.getTc()) ? i.getSc() : i.getSc() + " / " + i.getTc());
                    ans.setInfo(Map.of("query", ObfInt.encode(i.getId())));
                    return ans;
                }
        );
    }

    /**
     * 编辑词条的时候的明确的词条
     */
    public CiyuUpdate getCiyuById(int id, Dialect d)
    {
        return new CiyuUpdate(d,
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

        log.insertWord(d.toString(), JsonTool.toJson(getCiyuById(id, d)), JsonTool.toJson(data), "U");
    }

    public void createCiyu(CiyuCreate ci, Dialect d)
    {
        var model = ci.checkAndTransfer(d);

        for (var i : model)
        {
            if (Maybe.uncertain(cy.findByUniqueKey(i, d.toString())).isEmpty())
            {
                try
                {
                    cy.insertWord(i, d.toString());
                    log.insertWord(d.toString(), null, JsonTool.toJson(i), "C");
                } catch (DuplicateKeyException ignored)//幂等
                {
                }
            }
        }
    }

    public Maybe<ObfInt> getEditLinkIfExist(UString ciyu, Dialect d)
    {
        try
        {
            var entity = ListTool.checkSizeOne(cy.findCiyuByScOrTc(ciyu.toString(), d.toString(), Language.TC.toString()),
                    "没有找到词语", "查到了过多的数据");
            return Maybe.exist(ObfInt.encode(entity.getId()));
        } catch (NoSuchElementException e)
        {
            return Maybe.nothing();
        }
    }
}
