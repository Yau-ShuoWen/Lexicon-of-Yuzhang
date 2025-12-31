package com.shuowen.yuzong.service.impl.Pinyin;

import com.shuowen.yuzong.Linguistics.Scheme.Pinyin;
import com.shuowen.yuzong.Tool.TestTool.EqualChecker;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.mapper.IPA.IPAMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PinyinService
{
    @Autowired
    private IPAMapper m;


    /**
     * 获得目标拼音「音节」的所有字典的IPA版本
     */
    public Map<String, String> getAllIPASyllable(Pinyin p, Dialect d)
    {
        if (!p.isValid()) return Map.of();
        return Yinjie.of(m.findByPinyin(p.getPinyin(), d.toString())).getInfo();
    }


    /**
     * 获得目标拼音「音调」的所有字典的IPA版本
     */
    public Map<String, String> getAllIPATone(Pinyin p, Dialect d)
    {
        if (!p.isValid()) return Map.of();
        return Shengdiao.of(m.findByTone(p.getTone(), d.toString())).getInfo();
    }


    /**
     * 获得所有的字典代号，用于遍历
     *
     * @implNote 魔法字段 {@code ba} 只是最常见最会可能覆盖所有内容的读音
     */
    public Set<String> getDictionarySet(Dialect d)
    {
        return Yinjie.of(m.findByPinyin("ba", d.toString())).getInfo().keySet();
    }


    /**
     * 传入一条拼音，把所有字典版本的IPA全部转换出来
     *
     * @apiNote 只有两次查询，是最高效的版本
     */
    public Map<String, String> getAllIPA(Pinyin p, PinyinOption op, Dialect d)
    {
        return getMultiLine(Set.of(p), op, d).getOrDefault(p, Map.of());
    }


    /**
     * 传入多条拼音，把所有字典版本的IPA全部转换出来
     *
     * @apiNote 只有两次查询，是最高效的版本
     */
    public Map<Pinyin, Map<String, String>> getMultiLine(Set<Pinyin> p, PinyinOption op, Dialect d)
    {
        return IPATool.getMultiline(p, op.getTone(), op.getSyllable(), getDictionarySet(d), m::findAllPinyinList, m::findAllToneList, d);
    }

    public void insertSyllable(Pinyin p, Dialect d)
    {
        // 如果拼音无效，或者已经产生了数据
        if (!p.isValid()) return;
        if (!getAllIPA(p, PinyinOption.defaultOf(), d).isEmpty()) return;

        IPATool.insertSyllable(m::findSegment, m::insertPinyin, p, d);
    }

    /**
     * 测试
     */
    public EqualChecker<Yinjie> check(Dialect d)
    {
        return IPATool.checkIPA(m::findAllPinyin, m::findAllSegment, d);
    }

    public void updateIPA(Dialect d)
    {
        var answer = check(d);
        if (answer.allTrue()) System.out.println("没有需要更新的数据");
        else
        {
            System.out.println("以下数据要更新");
            answer.report();
            IPATool.updateIPA(m::findAllPinyin, m::findAllSegment, m::changeInfo, d);
            System.out.println("更新完成");
        }
    }
}
