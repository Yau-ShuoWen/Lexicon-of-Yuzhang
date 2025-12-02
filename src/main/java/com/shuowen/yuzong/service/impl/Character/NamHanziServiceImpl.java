package com.shuowen.yuzong.service.impl.Character;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Tool.DataVersionCtrl.SetCompareUtil;
import com.shuowen.yuzong.Tool.JavaUtilExtend.UniqueList;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.domain.Character.HanziEdit;
import com.shuowen.yuzong.data.domain.IPA.Phonogram;
import com.shuowen.yuzong.data.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.data.dto.Character.HanziOutline;
import com.shuowen.yuzong.data.dto.Character.HanziShow;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.mapper.Character.NamCharMapper;
import com.shuowen.yuzong.data.domain.Character.HanziEntry;
import com.shuowen.yuzong.data.model.Character.CharEntity;
import com.shuowen.yuzong.service.HanziService;
import com.shuowen.yuzong.service.impl.Pinyin.NamPinyinServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class NamHanziServiceImpl implements HanziService<NamStyle>
{
    /**
     * 查询汉字
     */
    @Autowired
    private NamCharMapper hz;

    /**
     * 查询音标
     */
    @Autowired
    private NamPinyinServiceImpl ipa;

    @Autowired
    private NamNotationServiceImpl mdr;

    /**
     * 查询匹配简体、繁体，获得结果集
     */
    public HanziEntry getHanziScTc(String hanzi)
    {
        return HanziEntry.of(hz.findHanziByScTc(hanzi));
    }


    /**
     * 查询匹配简体、繁体、模糊汉字，获得结果集
     */
    public HanziEntry getHanziVague(String hanzi)
    {
        return HanziEntry.of(hz.findHanziByVague(hanzi));
    }


    /**
     * 查询匹配确定的简体或繁体，获得结果集
     */
    public HanziEntry getHanziScOrTc(String hanzi, Language lang)
    {
        return HanziEntry.of(hz.findHanziByScOrTc(hanzi, lang.toString()));
    }


    /**
     * 获得的结果集根据需要的结果分类<p>
     * 请参阅： HanziEntry 里面的 split 方法
     */
    public List<HanziEntry> getHanziGroup(String hanzi, String lang, boolean vague)
    {
        return (vague ? getHanziVague(hanzi) : getHanziScTc(hanzi))
                .split(Language.of(lang));
    }

    /**
     * 获得结果的集根据分类结果合并内容
     */
    public List<HanziShow> getHanziOrganize(String hanzi, String lang, boolean vague)
    {
        return HanziShow.ListOf(getHanziGroup(hanzi, lang, vague));
    }

    /**
     * 对外接口：获得结果集，根据分类结果合并内容，并且按照要求渲染拼音或者国际音标
     */
    public List<HanziShow> getHanziFormatted
    (String hanzi, String lang, boolean vague,
     NamStyle style, Phonogram phonogram, IPAToneStyle ts, IPASyllableStyle ss)
    {
        List<HanziShow> res = getHanziOrganize(hanzi, lang, vague);
        HanziShow.initPinyinIPA(res, style, phonogram,
                ipa.getDefaultDict(), NamPinyin::of, ipa::getMultiLine, ts, ss);
        return res;
    }

    public List<SearchResult> getHanziSearch(String query, String lang, boolean vague)
    {
        UniqueList<SearchResult> ans = new UniqueList<>();
        for (String hanzi : UString.of(query))
        {
            for (var i : getHanziOrganize(hanzi, lang, vague))
            {
                SearchResult tmp = new SearchResult();
                tmp.setTitle(i.getHanzi());
                tmp.setTag("hanzi");
                tmp.setExlain("无");
                tmp.setInfo(Map.of("hanzi", i.getHanzi(), "lang", i.getLanguage()));
                ans.add(tmp);
            }
        }
        return ans.toList();
    }

    public HanziShow getHanzShow(String hanzi, String lang, Phonogram phonogram,
                                 IPAToneStyle ts, IPASyllableStyle ss)
    {
        Language l = Language.of(lang);
        var ans = HanziShow.ListOf(getHanziScOrTc(hanzi, l).split(l));

        if (ans.isEmpty()) return null;
        if (ans.size() > 1) throw new RuntimeException("参数有错误");

        HanziShow.initPinyinIPA(ans, NamStyle.getStandardStyle(),
                phonogram, ipa.getDefaultDict(), NamPinyin::of, ipa::getMultiLine, ts, ss);
        return ans.get(0);
    }

    public List<HanziOutline> filter(String query)
    {
        List<HanziOutline> ans = new ArrayList<>();

        for (String hanzi : UString.of(query))
        {
            HanziEntry entry = getHanziVague(hanzi);
            for (int i = 0; i < entry.getList().size(); i++)
                ans.add(entry.getItem(i).transfer());
        }
        return ans;
    }

    /**
     * 编辑词条的时候的明确的词条
     */
    public HanziEdit getHanziById(Integer id)
    {
        return HanziEdit.of(
                hz.findHanziByCharId(id),
                hz.findHanziSimilarByCharId(id),
                hz.findHanziPinyinByCharId(id),
                mdr.getEdit(id)
        );
    }

    @Transactional (rollbackFor = {Exception.class})
    public void editHanzi(HanziEdit he)
    {
        try
        {
            CharEntity ch = he.transfer();

            if ((ch.getId() == null || ch.getId() <= 0))
                hz.insertChar(ch);
            else hz.updateCharById(ch);


            int id = ch.getId();

            /* 对于similar和mulpy字段的流程：
             * 1. 统一设置id
             * 2. 比较并且处理
             * */
            for (var i : he.getSimilar()) i.setCharId(id);
            for (var i : SetCompareUtil.compare(
                    new HashSet<>(hz.findHanziSimilarByCharId(id)),
                    new HashSet<>(he.getSimilar())))
            {
                switch (i.getChangeType())
                {
                    case ADDED -> hz.insertCharSimilar(i.getNewItem());
                    case MODIFIED -> hz.updateCharSimilarById(i.getNewItem());
                    case DELETED -> hz.deleteCharSimilarById(i.getOldItem().getId());
                }
            }

            for (var i : he.getMulPy()) i.setCharId(id);
            for (var i : SetCompareUtil.compare(
                    new HashSet<>(hz.findHanziPinyinByCharId(id)),
                    new HashSet<>(he.getMulPy())))
            {
                switch (i.getChangeType())
                {
                    case ADDED -> hz.insertCharPinyin(i.getNewItem());
                    case MODIFIED -> hz.updateCharPinyinById(i.getNewItem());
                    case DELETED -> hz.deleteCharPinyinById(i.getOldItem().getId());
                }
            }

            for (var i : he.getMandarin())
            {
                i.setRightId(id);
            }

            mdr.edit(he.getMandarin());

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
