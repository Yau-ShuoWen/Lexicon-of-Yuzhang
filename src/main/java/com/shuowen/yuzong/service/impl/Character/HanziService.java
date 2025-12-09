package com.shuowen.yuzong.service.impl.Character;

import com.shuowen.yuzong.Tool.DataVersionCtrl.SetCompareUtil;
import com.shuowen.yuzong.Tool.JavaUtilExtend.UniqueList;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.domain.Character.HanziEdit;
import com.shuowen.yuzong.data.domain.Character.HanziEntry;
import com.shuowen.yuzong.data.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.data.domain.IPA.Phonogram;
import com.shuowen.yuzong.data.dto.Character.HanziOutline;
import com.shuowen.yuzong.data.dto.Character.HanziShow;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.mapper.Character.CharMapper;
import com.shuowen.yuzong.data.model.Character.CharEntity;
import com.shuowen.yuzong.service.impl.Pinyin.PinyinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class HanziService
{
    @Autowired
    private CharMapper hz;

    @Autowired
    private PinyinService ipa;

    @Autowired
    private PronunService mdr;

    /**
     * 查询匹配确定的简体或繁体，获得结果集
     */
    private HanziEntry getHanziScOrTc(String hanzi, Language lang, Dialect d)
    {
        return HanziEntry.of(hz.findHanziByScOrTc(hanzi, lang.toString(), d.toString()));
    }

    /**
     * 查询匹配简体或繁体，获得结果集
     */
    private HanziEntry getHanziScTc(String hanzi, Dialect d)
    {
        return HanziEntry.of(hz.findHanziByScTc(hanzi, d.toString()));
    }

    /**
     * 查询匹配简体、繁体、模糊汉字，获得结果集
     */
    private HanziEntry getHanziVague(String hanzi, Dialect d)
    {
        return HanziEntry.of(hz.findHanziByVague(hanzi, d.toString()));
    }

    /**
     * 获得结果的集根据分类结果合并内容
     *
     * @param grading 模糊识别粒度 1：{@code getHanziScOrTc}  2：{@code getHanziScTc}  3：{@code getHanziVague}
     */
    private List<HanziShow> getHanziOrganize(String hanzi, Language lang, Dialect d, int grading)
    {
        HanziEntry ans = switch (grading)
        {
            case 1 -> getHanziScOrTc(hanzi, lang, d);
            case 2 -> getHanziScTc(hanzi, d);
            case 3 -> getHanziVague(hanzi, d);
            default -> throw new RuntimeException("超范围");
        };
        return HanziShow.ListOf(ans.split(lang));
    }

    /**
     * 通过关键词，搜索出一系列搜索结果，但只保留基本信息
     */
    public List<SearchResult> getHanziSearchInfo(String query, Language lang, Dialect d, boolean vague)
    {
        UniqueList<SearchResult, SearchResult> ans = UniqueList.of();
        for (String hanzi : UString.of(query))
        {
            for (var i : getHanziOrganize(hanzi, lang, d, vague ? 3 : 2))
            {
                ans.add(new SearchResult(
                        i.getHanzi(), "无", "hanzi",
                        Map.of("hanzi", i.getHanzi(), "lang", i.getLanguage())
                ));
            }
        }
        return ans.getList();
    }

    /**
     * 精确的给出找的信息，获得汉字详细信息
     */
    public HanziShow getHanziDetailInfo(String hanzi, Language lang, Dialect d,
                                        Phonogram phonogram, IPAToneStyle ts, IPASyllableStyle ss)
    {
        var ans = getHanziOrganize(hanzi, lang, d, 1);

        if (ans.size() != 1)
            throw new RuntimeException(ans.size() > 1 ? "not unique 汉字不唯一" : "not found 未找到汉字");

        HanziShow.initPinyinIPA(ans, ipa.getStandardStyle(d),
                phonogram, ipa.getDefaultDict(d), ipa.getFactory(d), ipa::getMultiLine, ts, ss, d);
        return ans.get(0);
    }

    /**
     * 在编辑界面的时候，给一个非常宽松的筛选
     */
    public List<HanziOutline> getHanziFilterInfo(String query, Dialect d)
    {
        UniqueList<HanziOutline, HanziOutline> ans = UniqueList.of();
        for (String hanzi : UString.of(query))
        {
            // 这里不用getHanziOrganize是因为要词条原貌才能编辑，而不是合并后的显示结果
            HanziEntry entry = getHanziVague(hanzi, d);
            for (int i = 0; i < entry.getList().size(); i++)
                ans.add(entry.getItem(i).transfer());
        }
        return ans.getList();
    }

    /**
     * 编辑词条的时候的明确的词条
     */
    public HanziEdit getHanziById(Integer id, Dialect d)
    {
        return HanziEdit.of(
                hz.findHanziByCharId(id, d.toString()),
                hz.findHanziSimilarByCharId(id, d.toString()),
                hz.findHanziPinyinByCharId(id, d.toString()),
                mdr.getEdit(id, d)
        );
    }


    @Transactional (rollbackFor = {Exception.class})
    public void editHanzi(HanziEdit he, Dialect d)
    {
        CharEntity ch = he.transfer();

        // 通过唯一键寻找数据库里是否也有
        CharEntity maybe = hz.findByUniqueKey(ch, d.toString());

        // 如果没找到，说明是新增，可以插入
        // 如果id是同一个，那么说明是原地更新
        // 其他情况为新增但是唯一键冲突，那么说明是冲突，抛出异常
        if (maybe != null && !maybe.getId().equals(ch.getId()))
            throw new IllegalArgumentException("数据 简体：" + ch.getHanzi() + " 繁体：" + ch.getHantz() + " 拼音：" + ch.getStdPy() + " 重复");


        // 处理主表插入
        if ((ch.getId() == null || ch.getId() <= 0))
            hz.insertChar(ch, d.toString());
        else hz.updateCharById(ch, d.toString());


        int id = ch.getId();

        /* 对于similar和mulpy字段的流程：
         * 1. 统一设置id
         * 2. 比较并且处理
         * */
        for (var i : he.getSimilar()) i.setCharId(id);
        for (var i : SetCompareUtil.compare(
                new HashSet<>(hz.findHanziSimilarByCharId(id, d.toString())),
                new HashSet<>(he.getSimilar())))
        {
            switch (i.getChangeType())
            {
                case ADDED -> hz.insertCharSimilar(i.getNewItem(), d.toString());
                case MODIFIED -> hz.updateCharSimilarById(i.getNewItem(), d.toString());
                case DELETED -> hz.deleteCharSimilarById(i.getOldItem().getId(), d.toString());
            }
        }

        for (var i : he.getMulPy()) i.setCharId(id);
        for (var i : SetCompareUtil.compare(
                new HashSet<>(hz.findHanziPinyinByCharId(id, d.toString())),
                new HashSet<>(he.getMulPy())))
        {
            switch (i.getChangeType())
            {
                case ADDED -> hz.insertCharPinyin(i.getNewItem(), d.toString());
                case MODIFIED -> hz.updateCharPinyinById(i.getNewItem(), d.toString());
                case DELETED -> hz.deleteCharPinyinById(i.getOldItem().getId(), d.toString());
            }
        }

        for (var i : he.getMandarin())
        {
            i.setRightId(id);
        }

        // 普通话对应字段，丢给专门的类处理
        mdr.edit(he.getMandarin(), d);
    }
}
