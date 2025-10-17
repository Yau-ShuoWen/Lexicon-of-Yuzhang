package com.shuowen.yuzong.service.impl.Character;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Language;
import com.shuowen.yuzong.Tool.dataStructure.Status;
import com.shuowen.yuzong.dao.domain.Character.Hanzi;
import com.shuowen.yuzong.dao.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.dao.dto.HanziShow;
import com.shuowen.yuzong.dao.mapper.Character.NamCharMapper;
import com.shuowen.yuzong.dao.domain.Character.HanziEntry;
import com.shuowen.yuzong.service.HanziService;
import com.shuowen.yuzong.service.impl.pinyin.NamPinyinServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 用于程序内部调用，在其他表格里已经明确记录编号的时候，不需要模糊查询
     *
     * @return 返回的直接是确定的Hanzi
     */
    public Hanzi getHanziById(Integer id)
    {
        return Hanzi.of(hz.selectByPrimaryKey(id));
    }

    /**
     * 查询匹配简体、繁体，获得结果集
     */
    public HanziEntry getHanziScTc(String hanzi)
    {
        return HanziEntry.of(hz.findByHanziScTc(hanzi));
    }


    /**
     * 查询匹配简体、繁体、模糊汉字，获得结果集
     */
    public HanziEntry getHanziVague(String hanzi)
    {
        return HanziEntry.of(hz.findByHanziVague(hanzi));
    }


    /**
     * 获得的结果集根据需要的结果分类<p>
     * 请参阅： HanziEntry 里面的 split 方法
     */
    public List<HanziEntry> getHanziGroup(String hanzi, String lang, boolean certain)
    {
        return (certain ? getHanziScTc(hanzi) : getHanziVague(hanzi))
                .split(Language.of(lang));
    }

    /**
     * 获得结果的集根据分类结果合并内容
     */
    public List<HanziShow> getHanziOrganize(String hanzi, String lang, boolean certain)
    {
        return HanziShow.ListOf(getHanziGroup(hanzi, lang, certain));
    }

    /**
     * 查询匹配简体、繁体，获得结果集，根据分类结果合并内容，并且按照要求渲染拼音或者国际音标
     */
    public List<HanziShow> getHanziFormatted
    (String hanzi, String lang, boolean certain, NamStyle style, Status status, IPAToneStyle ms)
    {
        List<HanziShow> res = getHanziOrganize(hanzi, lang, certain);
        HanziShow.initPinyinIPA(res, style, status,
                ipa.getDefaultDict(), NamPinyin::of, ipa::getMultiLine, ms);
        return res;
    }
}
