package com.shuowen.yuzong.service.impl.Character;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Tool.dataStructure.Language;
import com.shuowen.yuzong.Tool.dataStructure.Status;
import com.shuowen.yuzong.dao.domain.Character.Hanzi;
import com.shuowen.yuzong.dao.mapper.Character.NamCharMapper;
import com.shuowen.yuzong.dao.domain.Character.HanziEntry;
import com.shuowen.yuzong.dao.domain.Character.dialect.NamHanzi;
import com.shuowen.yuzong.dao.model.Character.CharEntity;
import com.shuowen.yuzong.service.HanziService;
import com.shuowen.yuzong.service.impl.pinyin.NamPinyinServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;


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
    private NamPinyinServiceImpl ipa;


    /**
     * 批量获取 IPA 数据
     */
    private Map<NamPinyin, Map<String, String>> getIPABatch(Set<NamPinyin> pinyinList)
    {
        return ipa.getMultiLine(pinyinList);
    }

    /**
     * 根据数据库内容完成整个的初始化，补充内容：IPA有关（批量版本）
     */
    private NamHanzi NamHanziOf(CharEntity e, NamStyle s, Status st)
    {
        return NamHanzi.of(e, s, st, this::getIPABatch);
    }

    /**
     * 创建工厂函数，供HanziEntry使用
     * */
    private Function<CharEntity, NamHanzi> factory(NamStyle style, Status statue)
    {
        return (CharEntity e) -> NamHanziOf(e, style, statue);
    }

    /**
     * 用于程序内部调用，在其他表格里已经明确记录编号的时候，不需要模糊查询
     * */
    public NamHanzi getHanziById(Integer id, NamStyle style, Status statue)
    {
        return NamHanziOf(hz.selectByPrimaryKey(id), style, statue);
    }

    /**
     * 查询匹配简体、繁体，获得结果集
     * */
    public HanziEntry<NamHanzi> getHanziScTc(String hanzi, NamStyle style, Status statue)
    {
        return HanziEntry.of(hz.findByHanziScTc(hanzi), factory(style, statue));
    }

    /**
     * 查询匹配简体、繁体，获得结果集，根据需要的结果分类<p>
     * 请参阅： HanziEntry 里面的 split 方法
     * */
    public List<HanziEntry<NamHanzi>> getHanziScTcGroup(String hanzi, String lang, NamStyle style, Status statue)
    {
        return getHanziScTc(hanzi, style, statue).split(Language.of(lang));
    }

    /**
     * 查询匹配简体、繁体、模糊汉字，获得结果集
     * */
    public HanziEntry<NamHanzi> getHanziVague(String hanzi, NamStyle style, Status statue)
    {
        return HanziEntry.of(hz.findByHanziVague(hanzi), factory(style, statue));
    }

    /**
     * 查询匹配简体、繁体，模糊识别，获得结果集，根据需要的结果分类<p>
     * 请参阅： HanziEntry 里面的 split 方法
     * */
    public List<HanziEntry<NamHanzi>> getHanziVagueGroup(String hanzi, String lang, NamStyle style, Status statue)
    {
        return getHanziVague(hanzi, style, statue).split(Language.of(lang));
    }


}
