package com.shuowen.yuzong.service.impl.pinyin;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.dao.domain.IPA.IPATool;
import com.shuowen.yuzong.dao.domain.IPA.Shengdiao;
import com.shuowen.yuzong.dao.domain.IPA.Yinjie;
import com.shuowen.yuzong.dao.mapper.PinyinIPA.NamIPAMapper;
import com.shuowen.yuzong.dao.model.PinyinIPA.IPASyllableEntry;
import com.shuowen.yuzong.dao.model.PinyinIPA.IPAToneEntry;
import com.shuowen.yuzong.service.PinyinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 南昌话拼音服务类
 * */
@Service
public class NamPinyinServiceImpl implements PinyinService<NamPinyin, NamStyle>
{
    @Autowired
    private NamIPAMapper m;

    /**
     * 查找对应「音节」在目标中的词典的IPA<br>
     * @apiNote 在查询所有信息的时候应该使用 {@code getAllIPASyllable}，然后使用 {@code getDictonarySet}遍历
     * */
    public String getIPASyllable(NamPinyin p, String dict)
    {
        if (!p.isValid()) return UniPinyin.getError();
        return Yinjie.of(m.findByPinyin(p.getPinyin())).getInfo(dict);
    }

    /**
     * 查找对应「音调」在目标中的词典的IPA（数字，不是五度标记）<br>
     * @apiNote 在查询所有信息的时候应该使用 {@code getAllIPATone}，然后使用 {@code getDictonarySet}遍历
     * */
    public String getIPATone(NamPinyin p, String dict)
    {
        if (!p.isValid()) return UniPinyin.getError();
        return Shengdiao.of(m.findByTone(p.getTone())).getInfo(dict);
    }

    /**
     * 查询目标拼音在目标词典中的IPA
     * @apiNote 这个和以上两个仅用于测试，实际上效率比较低
     * */
    public String getIPA(NamPinyin p, String dict)
    {
        if (!p.isValid()) return UniPinyin.getError();
        return IPATool.merge(
                Yinjie.of(m.findByPinyin(p.getPinyin())),
                Shengdiao.of(m.findByTone(p.getTone())),
                dict
        );
    }


    /**
     * 获得目标拼音「音节」的所有字典的IPA版本
     * */
    public Map<String, String> getAllIPASyllable(NamPinyin p)
    {
        if (!p.isValid()) return Map.of();
        return Yinjie.of(m.findByPinyin(p.getPinyin())).getInfo();
    }

    /**
     * 获得目标拼音「音调」的所有字典的IPA版本
     * */
    public Map<String, String> getAllIPATone(NamPinyin p)
    {
        if (!p.isValid()) return Map.of();
        return Shengdiao.of(m.findByTone(p.getTone())).getInfo();
    }

    /**
     * 获得所有的字典代号，用于遍历
     * @implNote 魔法字段 {@code ba} 只是最常见最会可能覆盖所有内容的读音
     * */
    public Set<String> getDictonarySet()
    {
        return Yinjie.of(m.findByPinyin("ba")).getInfo().keySet();
    }


    /**
     * 传入一条拼音，把所有字典版本的IPA全部转换出来
     * @apiNote 只有两次查询，是最高效的版本
     * */
    public Map<String, String> getAllIPA(NamPinyin p)
    {
        return getMultiLine(Set.of(p)).getOrDefault(p, Map.of());
    }

    /**
     * 传入多条拼音，把所有字典版本的IPA全部转换出来
     * @apiNote 只有两次查询，是最高效的版本
     * */
    public Map<NamPinyin, Map<String, String>> getMultiLine(Set<NamPinyin> p)
    {
        // 结果
        Map<NamPinyin, Map<String, String>> map = new HashMap<>();
        // 声母韵母集合
        Set<String> syllable = new HashSet<>(), tone = new HashSet<>();
        for (var pinyin : p)
        {
            if (pinyin.isValid())
            {
                syllable.add(pinyin.getPinyin());
                tone.add(pinyin.getTone().toString());
            }
        }

        // mapper查询，成为可以查的字典
        Map<String, IPASyllableEntry> syllableMap = new HashMap<>();
        Map<Integer, IPAToneEntry> toneMap = new HashMap<>();

        //如果查询的为空，那么会异常，接受异常后直接返回空集合
        try
        {
            for (var i : m.findAllPinyinList(syllable))
                syllableMap.put(i.getStandard(), i);
            for (var i : m.findAllToneList(tone))
                toneMap.put(i.getStandard(), i);
        }catch (Exception e)
        {
            return Map.of();
        }

        for (var pinyin : p)
        {
            if (pinyin.isValid())
            {
                Map<String, String> tmp = new HashMap<>();
                for (var dict : getDictonarySet())
                {
                    tmp.put(dict,
                            IPATool.merge(
                                    Yinjie.of(syllableMap.get(pinyin.getPinyin())),
                                    Shengdiao.of(toneMap.get(pinyin.getTone())),
                                    dict
                            ));
                }
                map.put(pinyin, tmp);
            }
        }
        return map;
    }

    /**
     * 传入风格，返回这个风格的诗歌的示例（暂时只有这一首）
     * */
    public String getPreview(NamStyle style)
    {
        String s = "枫桥夜泊\n月落乌啼霜满天，江枫渔火对愁眠。\n姑苏城外寒山寺，夜半钟声到客船。\n";
        String p = """
                [fung1][qieu2][ia5][bok6]
                [nvot6][lok6][u1][ti2][song1][man3][tien1]
                [gong1][fung1][v4][fo3][dui4][ceu2][mien4]
                [gu1][su1][ceen2][uai5][hon2][san1][sii5]
                [ia5][pon5][zung1][seen1][tau5][kak6][con2]""";

        return s + NamPinyin.parseAndReplace(p, style);
    }
}
