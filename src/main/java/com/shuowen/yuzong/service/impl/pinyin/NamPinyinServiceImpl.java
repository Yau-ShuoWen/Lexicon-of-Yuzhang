package com.shuowen.yuzong.service.impl.pinyin;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.dao.domain.IPA.*;
import com.shuowen.yuzong.dao.mapper.PinyinIPA.NamIPAMapper;
import com.shuowen.yuzong.service.PinyinService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 南昌话拼音服务类
 */
@Service
public class NamPinyinServiceImpl implements PinyinService<NamPinyin, NamStyle>
{
    @Autowired
    private NamIPAMapper m;

    public String getDefaultDict()
    {
        return "ncdict";
    }

    /**
     * 获得目标拼音「音节」的所有字典的IPA版本
     */
    public Map<String, String> getAllIPASyllable(NamPinyin p)
    {
        if (!p.isValid()) return Map.of();
        return Yinjie.of(m.findByPinyin(p.getPinyin())).getInfo();
    }

    /**
     * 获得目标拼音「音调」的所有字典的IPA版本
     */
    public Map<String, String> getAllIPATone(NamPinyin p)
    {
        if (!p.isValid()) return Map.of();
        return Shengdiao.of(m.findByTone(p.getTone())).getInfo();
    }

    /**
     * 获得所有的字典代号，用于遍历
     *
     * @implNote 魔法字段 {@code ba} 只是最常见最会可能覆盖所有内容的读音
     */
    public Set<String> getDictionarySet()
    {
        return Yinjie.of(m.findByPinyin("ba")).getInfo().keySet();
    }

    /**
     * 传入一条拼音，把所有字典版本的IPA全部转换出来
     *
     * @apiNote 只有两次查询，是最高效的版本
     */
    public Map<String, String> getAllIPA(NamPinyin p, IPAToneStyle ts, IPASyllableStyle ss)
    {
        return getMultiLine(Set.of(p), ts, ss).getOrDefault(p, Map.of());
    }


    /**
     * 传入多条拼音，把所有字典版本的IPA全部转换出来
     *
     * @apiNote 只有两次查询，是最高效的版本
     * @see IPATool
     */
    public Map<NamPinyin, Map<String, String>> getMultiLine(Set<NamPinyin> p, IPAToneStyle ts, IPASyllableStyle ss)
    {
        return IPATool.getMultiline(p, ts, ss, getDictionarySet(), m::findAllPinyinList, m::findAllToneList);
    }

    public void insertSyllable(NamPinyin p)
    {
        // 如果拼音无效，或者已经产生了数据
        if (!p.isValid()) return;
        if (!getAllIPA(p, IPAToneStyle.FIVE_DEGREE_LINE, IPASyllableStyle.CHINESE_SPECIAL).isEmpty()) return;

        IPATool.insertSyllable(p, m::findElement, m::insertPinyin);
    }

    /**
     * 测试
     */
    public Pair<Map<String, Integer>, Set<String>> check()
    {
        return IPATool.check(m::findAllPinyin, m::findAllElement, NamPinyin::of);
    }

    public void updateIPA()
    {
        int num = check().getRight().size();
        if (num == 0)
        {
            System.out.println("没有需要更新的数据");
            return;
        }
        System.out.println("开始更新 " + num + " 条数据");
        // 执行更新
        IPATool.updateIPA(m::findAllPinyin, m::findAllElement, NamPinyin::of, m::changeInfo);
        System.out.println("更新完成");
    }


    /**
     * 传入风格，返回这个风格的诗歌的示例（暂时只有这一首）
     */
    public String getPreview(NamStyle style)
    {
        String s = "枫桥夜泊\n月落乌啼霜满天，江枫渔火对愁眠。\n姑苏城外寒山寺，夜半钟声到客船。\n";
        String p = """
                [fung1][qieu2][ia5][pok6]
                [nvot6][lok6][u1][ti2][song1][mon3][tien1]
                [gong1][fung1][v4][fo3][dui4][ceu2][mien4]
                [gu1][su1][ceen2][uai5][hon2][san1][sii5]
                [ia5][bon5][zung1][seen1][tau5][kak6][cuon2]""";

        return s + NamPinyin.parseAndReplace(p, style);
    }
}
