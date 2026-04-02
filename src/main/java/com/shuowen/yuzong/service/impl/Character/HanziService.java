package com.shuowen.yuzong.service.impl.Character;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Tool.DataVersionCtrl.SetCompareUtil;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.UniqueList;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UChar;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.ObfInt;
import com.shuowen.yuzong.Tool.format.ObfString;
import com.shuowen.yuzong.data.domain.Character.HanziCreate;
import com.shuowen.yuzong.data.domain.Character.HanziUpdate;
import com.shuowen.yuzong.data.domain.Character.HanziGroup;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.domain.Character.HanziShow;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.mapper.Character.HanziMapper;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HanziService
{
    @Autowired
    private HanziMapper hz;

    @Autowired
    private PronunService mdr;

    // 查询界面 ---------------------------------------------------------------------------------------------------------

    /**
     * 通过关键词，搜索出一系列搜索结果，但只保留基本信息
     */
    public List<SearchResult> getHanziSearchInfo(String query, Language l, Dialect d, boolean vague)
    {
        UniqueList<SearchResult, SearchResult> ans = UniqueList.of();
        for (UChar hanzi : UString.of(query))
        {
            var item = HanziGroup.listOf(
                    vague ? hz.findHanziByVague(hanzi.toString(), d.toString()) :
                            hz.findHanziByScTc(hanzi.toString(), d.toString())
                    , l, d
            );

            for (var i : item)
            {
                // 标题 = 字 + 拼音，如果不止一个主读音，使用" / "拼接
                String title = i.getHanzi() + "  " +
                        i.getPinyin().stream().map(RPinyin::toString).collect(Collectors.joining(" / "));

                // 标签为加粗体【字】
                // - 如果查询字不等于查询到的汉字，说明模糊识别，先转移，在拼接
                // - 如果存在特殊的汉字，展示出来
                String explain = "{b 【字】}  ";
                if (!i.getHanzi().equals(hanzi)) explain += "根據{b 「%s」}模糊識別；";
                if (i.isSpecial()) explain += "存在{b 特殊用法}；";
                explain = StringTool.deleteBack(explain);
                explain = String.format(ScTcText.get(explain, l).toString(), hanzi);


                var tmp = new SearchResult();
                tmp.setTitle(title);
                tmp.setExplain(explain);
                tmp.setTag("hanzi");
                tmp.setInfo(Map.of("query", ObfString.encode(i.getHanzi().toString())));
                ans.add(tmp);
            }
        }
        return ans.getList();
    }

    /**
     * 精确的给出找的信息，获得汉字详细信息
     */
    public HanziShow getHanziDetailInfo(String hanzi, Language l, Dialect d, PinyinOption op)
    {
        var item = ListTool.checkSizeOne(
                HanziGroup.listOf(hz.findHanziByScOrTc(hanzi, l.toString(), d.toString()), l, d),
                "not found 未找到汉字", "not unique 汉字不唯一"
        );
        return HanziShow.of(item, new IPAData(l, d, op));
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
                tmp.setExplain(i.getMainPy());
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
        return new HanziUpdate(
                hz.findHanziByCharId(id, d.toString()),
                hz.findHanziSimilarByCharId(id, d.toString()),
                hz.findHanziPinyinByCharId(id, d.toString()),
                mdr.getHanziSelected(id, d)
        );
    }


    @Transactional (rollbackFor = {Exception.class})
    public void editHanzi(HanziUpdate he, Dialect d)
    {
        var data = he.checkAndTransfer(d);

        var ch = data.getFirst();
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
                    拼音：%s
                    已经有另外一条数据，这三段内容和这个完全相同了。
                    请在那一条数据里修改。
                    """, ch.getSc(), ch.getTc(), ch.getMainPy()
            ));

        // 主表更新
        hz.updateCharById(ch, d.toString());

        /* 对于similar和mulpy字段的流程：
         * 1. 统一设置id
         * 2. 比较并且处理
         * */
        var sim = data.getSecond();
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

        var py = data.getThird();
        for (var i : SetCompareUtil.compare(
                new HashSet<>(hz.findHanziPinyinByCharId(id, d.toString())),
                new HashSet<>(py)))
        {
            switch (i.getChangeType())
            {
                case ADDED -> hz.insertCharPinyin(i.getNewItem(), d.toString());
                case MODIFIED -> hz.updateCharPinyinById(i.getNewItem(), d.toString());
                case DELETED -> hz.deleteCharPinyinById(i.getOldItem().getId(), d.toString());
            }
        }

        // 普通话对应字段，给专门的类处理
        mdr.handleEdit(data.getFourth(), d);
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

        var pyModel = model.getRight();

        for (var i : model.getLeft())
        {
            if (Maybe.uncertain(hz.findByUniqueKey(i, d.toString())).isEmpty())
            {
                try
                {
                    hz.insertChar(i, d.toString());
                    int id = i.getId();

                    pyModel.setCharId(id);
                    hz.insertCharPinyin(pyModel, d.toString());

                } catch (DuplicateKeyException ignored)//幂等
                {
                }
            }
        }
    }

    public List<String> getHanziMenu(String text, Dialect d)
    {
        var list = mdr.getHanzisByPinyin(text);
        if(list==null) return List.of("拼音无效");
        List<String> ans = new ArrayList<>();

        // 获得所有汉字
        Set<String> set = new HashSet<>();
        for (var i : list) for (var j : i.getRight()) set.add(j.toString());
        if(set.isEmpty()) return List.of("没有找到");
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
                if (existChars.contains(j)) ok+=j.toString();
                else no+=j.toString();
            }
            ans.add(String.format("%s %s（%s）", i.getLeft().getRead(), no, ok));
        }
        return ans;
    }
}
