package com.shuowen.yuzong.service.impl;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.dao.mapper.Character.NamCharMapper;
import com.shuowen.yuzong.dao.domain.Character.HanziEntry;
import com.shuowen.yuzong.dao.domain.Character.dialect.NamHanzi;
import com.shuowen.yuzong.service.Interface.HanziService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class NamHanziServiceImpl implements HanziService<NamStyle, NamHanzi>
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
    private NamPYServiceImpl ipa;


    /**
     * 用于程序内部调用，在其他表格里已经明确记录编号的时候，不需要模糊查询
     * */
    public NamHanzi getHanziById(Integer id, NamStyle style)
    {
        return NamHanzi.of(hz.selectByPrimaryKey(id), style);
    }

    /**
     * 查询匹配简体、繁体，获得结果集
     * */
    public HanziEntry<NamHanzi> getHanziScTc(String hanzi, NamStyle style)
    {
        return HanziEntry.of(hz.findByHanziScTc(hanzi), e -> new NamHanzi(e, style));
    }

    /**
     * 查询匹配简体、繁体，获得结果集，根据需要的结果分类<p>
     * 请参阅： HanziEntry 里面的 split 方法
     * */
    public List<HanziEntry<NamHanzi>> getHanziScTcGroup(String hanzi, String lang, NamStyle style)
    {
        return getHanziScTc(hanzi, style).split(lang);
    }

    /**
     * 查询匹配简体、繁体、模糊汉字，获得结果集
     * */
    public HanziEntry<NamHanzi> getHanziVague(String hanzi, NamStyle style)
    {
        return HanziEntry.of(hz.findByHanziVague(hanzi), e -> new NamHanzi(e, style));
    }

    /**
     * 查询匹配简体、繁体，获得结果集，根据需要的结果分类<p>
     * 请参阅： HanziEntry 里面的 split 方法
     * */
    public List<HanziEntry<NamHanzi>> getHanziVagueGroup(String hanzi, String lang, NamStyle style)
    {
        return getHanziVague(hanzi, style).split(lang);
    }
}
