package com.shuowen.yuzong.service.impl.Character;

import com.shuowen.yuzong.Tool.DataVersionCtrl.SetCompareUtil;
import com.shuowen.yuzong.Tool.JavaUtilExtend.UniqueList;
import com.shuowen.yuzong.Tool.Obfuscation;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.Character.HanziUpdate;
import com.shuowen.yuzong.data.domain.Character.HanziGroup;
import com.shuowen.yuzong.data.domain.Character.HanziCreate;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.domain.Character.HanziShow;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.mapper.Character.HanziMapper;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import com.shuowen.yuzong.service.impl.IPA.IPAService;
import com.shuowen.yuzong.service.impl.Refer.ReferServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class HanziService
{
    @Autowired
    private HanziMapper hz;

    @Autowired
    private IPAService ipa;

    @Autowired
    private PronunService mdr;

    @Autowired
    private ReferServiceImpl refer;

    /**
     * 获得结果的集根据分类结果合并内容
     *
     * @param grading 模糊识别粒度
     *                <br>1：查询匹配确定的简体或繁体
     *                <br>2：查询匹配简体或繁体
     *                <br>3：查询匹配简体、繁体、模糊汉字
     */
    private HanziGroup getHanziOrganize(String hanzi, Language l, Dialect d, int grading)
    {
        var res = switch (grading)
        {
            case 1 -> hz.findHanziByScOrTc(hanzi, l.toString(), d.toString());
            case 2 -> hz.findHanziByScTc(hanzi, d.toString());
            case 3 -> hz.findHanziByVague(hanzi, d.toString());
            default -> throw new RuntimeException("超范围");
        };
        return HanziGroup.of(res, l);
    }

    /**
     * 通过关键词，搜索出一系列搜索结果，但只保留基本信息
     */
    public List<SearchResult> getHanziSearchInfo(String query, Language l, Dialect d, boolean vague)
    {
        UniqueList<SearchResult, SearchResult> ans = UniqueList.of();
        for (String hanzi : UString.of(query))
        {
            for (var i : getHanziOrganize(hanzi, l, d, vague ? 3 : 2).getList())
            {
                var tmp = new SearchResult();
                tmp.setTitle(i.get(0).getHanzi());
                tmp.setExplain("");
                tmp.setTag("hanzi");
                tmp.setInfo(Map.of("hanzi", Obfuscation.encode(i.get(0).getHanzi()), "lang", l.toString()));

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
        return HanziShow.of(
                getHanziOrganize(Obfuscation.decode(hanzi), l, d, 1),
                new IPAData(l, d, op, refer::getDictMap, ipa::getIPA)
        );
    }

    /**
     * 在编辑界面的时候，给一个非常宽松的筛选
     */
    public List<SearchResult> getHanziFilterInfo(String query, Dialect d)
    {
        UniqueList<SearchResult, SearchResult> ans = UniqueList.of();
        for (String hanzi : UString.of(query))
        {
            for (var i : hz.findHanziByVague(hanzi, d.toString()))
            {
                var tmp = new SearchResult();

                // 相同显示一个："文" ，不同显示两个："车 / 車"
                tmp.setTitle(Objects.equals(i.getSc(), i.getTc()) ?
                        i.getSc() : i.getSc() + " / " + i.getTc());
                tmp.setExplain(i.getMainPy());
                tmp.setTag(Obfuscation.encodeInt(i.getId()));
                tmp.setInfo(Map.of());

                ans.add(tmp);
            }
        }
        return ans.getList();
    }

    /**
     * 编辑词条的时候的明确的词条
     */
    public HanziUpdate getHanziById(String idStr, Dialect d)
    {
        int id = Obfuscation.decodeInt(idStr);
        return HanziUpdate.of(
                hz.findHanziByCharId(id, d.toString()),
                hz.findHanziSimilarByCharId(id, d.toString()),
                hz.findHanziPinyinByCharId(id, d.toString()),
                mdr.getHanziSelected(id, d)
        );
    }


    @Transactional (rollbackFor = {Exception.class})
    public void editHanzi(HanziUpdate he, Dialect d)
    {
        he.check(d);

        HanziEntity ch = he.transfer();

        // 通过唯一键寻找数据库里是否也有
        HanziEntity maybe = hz.findByUniqueKey(ch, d.toString());

        // 如果没找到（maybe == null），说明是新增，可以插入
        // 如果id是同一个（id == id），那么说明是原地更新
        // 其他情况为新增但是唯一键冲突，那么说明是冲突，抛出异常
        if (maybe != null && !maybe.getId().equals(ch.getId()))
            throw new IllegalArgumentException("数据 简体：" + ch.getSc() + " 繁体：" + ch.getTc() + " 拼音：" + ch.getMainPy() + " 重复");


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

        for (var i : he.getVariantPy()) i.setCharId(id);
        for (var i : SetCompareUtil.compare(
                new HashSet<>(hz.findHanziPinyinByCharId(id, d.toString())),
                new HashSet<>(he.getVariantPy())))
        {
            switch (i.getChangeType())
            {
                case ADDED -> hz.insertCharPinyin(i.getNewItem(), d.toString());
                case MODIFIED -> hz.updateCharPinyinById(i.getNewItem(), d.toString());
                case DELETED -> hz.deleteCharPinyinById(i.getOldItem().getId(), d.toString());
            }
        }

        for (var i : he.getMandarin()) i.setDialectId(id);

        // 普通话对应字段，丢给专门的类处理
        mdr.handleEdit(he.getMandarin(), d);
    }

    public Pair<Maybe<String>, Maybe<String>> getNearBy(String id, Dialect d)
    {
        int idNum = Obfuscation.decodeInt(id); //解码
        var prevId = Maybe.uncertain(hz.findPrevId(idNum, d.toString()));
        var nextId = Maybe.uncertain(hz.findNextId(idNum, d.toString()));

        return Pair.of(  // 编码
                prevId.handleIfExist(Obfuscation::encodeInt),
                nextId.handleIfExist(Obfuscation::encodeInt)
        );
    }
//
//    @Transactional (rollbackFor = {Exception.class})
//    public void initHanzi(HanziCreate he, Dialect d)
//    {
//        he.check(d);
//
//        var model = he.transfer();
//        var pyModel = model.getRight();
//
//        for (var i : model.getLeft())
//        {
//            if (Maybe.uncertain(hz.findByUniqueKey(i, d.toString())).isEmpty())
//            {
//                hz.insertChar(i, d.toString());
//                int id = i.getId();
//
//                pyModel.setCharId(id);
//                hz.insertCharPinyin(pyModel, d.toString());
//            }
//        }
//    }
}
