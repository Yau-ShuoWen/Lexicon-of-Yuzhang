package com.shuowen.yuzong.dict.hanzi.service;

import com.shuowen.yuzong.dict.data.domain.Pinyin.PinyinConfig;
import com.shuowen.yuzong.dict.data.dto.SearchResult;
import com.shuowen.yuzong.dict.data.mapper.LogMapper;
import com.shuowen.yuzong.dict.hanzi.domain.*;
import com.shuowen.yuzong.dict.hanzi.mapper.HanziMapper;
import com.shuowen.yuzong.dict.hanzi.model.HanziEntity;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.ext.list.UniqueList;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.obfuscate.ObfInt;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UChar;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.Twin;
import com.shuowen.yuzong.util.version.SetCompareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class HanziService
{
    @Autowired
    private HanziMapper hz;

    @Autowired
    private PronunService mdr;

    @Autowired
    private LogMapper log;


    // 查询界面 ---------------------------------------------------------------------------------------------------------

    /**
     * 通过关键词，搜索出一系列搜索结果，但只保留基本信息
     */
    public List<SearchResult> getHanziSearchInfo(UString query, Language l, Dialect d)
    {
        UniqueList<SearchResult, SearchResult> ans = UniqueList.of();
        for (UChar hanzi : query)
        {
            var item = HanziGroup.listOf(hz.findHanziByVague(hanzi.toString(), d.toString()), l, d);

            for (var i : item)
            {
                var tmp = new SearchResult();
                tmp.setTitle(String.format("%s【%s】", i.getHanzi(), ScTcText.get("漢字", "汉字", l)));
                tmp.setExplain(i.getPinyin());
                tmp.setSpecial(i.isSpecial());
                tmp.setTag("hanzi");
                tmp.setInfo(Map.of("query", i.getHanzi()));
                ans.add(tmp);
            }
        }
        return ans.getList();
    }

    /**
     * 精确的给出找的信息，获得汉字详细信息
     */
    public HanziShow getHanziDetailInfo(UChar hanzi, Language l, Dialect d, PinyinConfig op)
    {
        var item = ListTool.checkSizeOne(
                HanziGroup.listOf(hz.findHanziByScOrTc(hanzi.toString(), l.toString(), d.toString()), l, d),
                "not found 未找到汉字", "not unique 汉字不唯一"
        );
        var chars = item.getData().get(0).getHanzis();
        return HanziShow.of(item, op, mdr.getRawCandidates(chars.getSc().toString(), chars.getTc().toString()));
    }


    // 编辑界面 ---------------------------------------------------------------------------------------------------------

    /**
     * 在编辑界面的时候，给一个非常宽松的筛选
     */
    public List<SearchResult> getHanziFilterInfo(String query, Dialect d)
    {
        UniqueList<SearchResult, SearchResult> ans = UniqueList.of();
        for (String hanzi : UString.of(query).chars())
        {
            for (var i : hz.findHanziByVague(hanzi, d.toString()))
            {
                var tmp = new SearchResult();

                // 相同显示一个："文" ，不同显示两个："车 / 車"
                tmp.setTitle(Objects.equals(i.getSc(), i.getTc()) ?
                        i.getSc() : i.getSc() + " / " + i.getTc());
                var pinyin = JsonTool.readJson(i.getPinyin(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<HanziPronunciation>>() {});
                tmp.setExplain(String.join("/", ListTool.mapping(pinyin,
                        py -> d.trustedCreatePinyin(py.getPinyin().toString()).toRPinyin().toString())));
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
    public HanziUpdate getHanziById(int id, Dialect d)
    {
        var entity = hz.findHanziByCharId(id, d.toString());
        if (entity == null) return null;
        return new HanziUpdate(d, entity, hz.findHanziSimilarByCharId(id, d.toString()));
    }


    @Transactional (rollbackFor = {Exception.class})
    public void editHanzi(HanziUpdate he, Dialect d)
    {
        var before = getHanziById(he.getId(), d);
        var candidates = mdr.getRawCandidates(he.getHanzi().getSc().toString(), he.getHanzi().getTc().toString());
        if (he.getPinyin().size() == 1 && candidates.size() == 1)
            he.getPinyin().get(0).setMandarin(new ArrayList<>());
        var data = he.checkAndTransfer(d);
        var ch = data.getLeft();
        int id = ch.getId();

        // 通过唯一键寻找数据库里是否也有
        HanziEntity maybe = hz.findByUniqueKey(ch, d.toString());

        // 如果没找到（maybe == null），说明是新增，但是这里是编辑
        if (maybe == null) throw new IllegalArgumentException("没有对应数据，请新增后在修改");

        // 如果id不相等，那说明两条数据唯一键冲突
        if (id != maybe.getId())
            throw new IllegalArgumentException(String.format("""
                    数据重复：
                    简体：%s
                    繁体：%s
                    已经有另外一条相同简繁体的汉字数据。
                    请在那一条数据里修改。
                    """, ch.getSc(), ch.getTc()
            ));

        mdr.validateMappings(he.getPinyin(), id, ch.getSc(), ch.getTc(), d);
        // 主表更新
        hz.updateCharById(ch, d.toString());

        /* 对于similar和mulpy字段的流程：
         * 1. 统一设置id
         * 2. 比较并且处理
         * */
        var sim = data.getRight();
        for (var i : SetCompareUtil.compare(
                new HashSet<>(hz.findHanziSimilarByCharId(id, d.toString())),
                new HashSet<>(sim)))
        {
            switch (i.getChangeType())
            {
                case ADDED -> hz.insertCharSimilar(i.getNewItem(), d.toString());
                case MODIFIED -> hz.updateCharSimilarById(i.getNewItem(), d.toString());
                case DELETED -> hz.deleteCharSimilarById(i.getOldItem().getId(), d.toString());
            }
        }

        log.insertChar(d.toString(), JsonTool.toJson(before), JsonTool.toJson(getHanziById(id, d)), "U");
    }

    public Twin<Maybe<ObfInt>> getNearBy(int id, Dialect d)
    {
        var prevId = Maybe.uncertain(hz.findPrevId(id, d.toString()));
        var nextId = Maybe.uncertain(hz.findNextId(id, d.toString()));

        return Twin.of(  // 编码
                prevId.handleIfExist(ObfInt::encode),
                nextId.handleIfExist(ObfInt::encode)
        );
    }

    @Transactional (rollbackFor = {Exception.class})
    public void createHanzi(HanziCreate he, Dialect d)
    {
        var model = he.checkAndTransfer(d);
        for (var i : model)
        {
            if (Maybe.uncertain(hz.findByUniqueKey(i, d.toString())).isEmpty())
            {
                try
                {
                    hz.insertChar(i, d.toString());
                    log.insertChar(d.toString(), null, JsonTool.toJson(i), "C");
                } catch (DuplicateKeyException ignored)//幂等
                {
                }
            }
        }
    }

    public List<String> getHanziMenu(String text, Dialect d)
    {
        var list = mdr.getHanzisByPinyin(text);
        if (list == null) return List.of("拼音无效");
        List<String> ans = new ArrayList<>();

        // 获得所有汉字
        Set<String> set = new HashSet<>();
        for (var i : list) for (var j : i.getRight()) set.add(j.toString());
        if (set.isEmpty()) return List.of("没有找到");
        // 获得
        Set<UChar> existChars = new HashSet<>();
        for (var i : hz.findHanziByScTcBatch(set, d.toString()))
        {
            existChars.add(UChar.of(i.getSc()));
            existChars.add(UChar.of(i.getTc()));
        }

        for (var i : list)
        {
            String ok = "", no = "";
            for (var j : i.getRight())
            {
                if (existChars.contains(j)) ok += j.toString();
                else no += j.toString();
            }
            ans.add(String.format("%s %s（%s）", i.getLeft().getRead(), no, ok));
        }
        return ans;
    }
}
