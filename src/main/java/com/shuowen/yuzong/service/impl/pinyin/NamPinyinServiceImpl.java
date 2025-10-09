package com.shuowen.yuzong.service.impl.pinyin;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.dao.domain.IPA.IPATool;
import com.shuowen.yuzong.dao.domain.IPA.Shengdiao;
import com.shuowen.yuzong.dao.domain.IPA.Yinjie;
import com.shuowen.yuzong.dao.domain.IPA.YinjiePart;
import com.shuowen.yuzong.dao.mapper.PinyinIPA.NamIPAMapper;
import com.shuowen.yuzong.dao.model.IPA.IPASyllableEntity;
import com.shuowen.yuzong.dao.model.IPA.IPAToneEntity;
import com.shuowen.yuzong.service.PinyinService;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Fail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

/**
 * 南昌话拼音服务类
 */
@Service
public class NamPinyinServiceImpl implements PinyinService<NamPinyin, NamStyle>
{
    @Autowired
    private NamIPAMapper m;

    /**
     * 查找对应「音节」在目标中的词典的IPA<br>
     *
     * @apiNote 在查询所有信息的时候应该使用 {@code getAllIPASyllable}，然后使用 {@code getDictonarySet}遍历
     */
    public String getIPASyllable(NamPinyin p, String dict)
    {
        if (!p.isValid()) return UniPinyin.getError();
        return Yinjie.of(m.findByPinyin(p.getPinyin())).getInfo(dict);
    }

    /**
     * 查找对应「音调」在目标中的词典的IPA（数字，不是五度标记）<br>
     *
     * @apiNote 在查询所有信息的时候应该使用 {@code getAllIPATone}，然后使用 {@code getDictonarySet}遍历
     */
    public String getIPATone(NamPinyin p, String dict)
    {
        if (!p.isValid()) return UniPinyin.getError();
        return Shengdiao.of(m.findByTone(p.getTone())).getInfo(dict);
    }

    /**
     * 查询目标拼音在目标词典中的IPA
     *
     * @apiNote 这个和以上两个仅用于测试，实际上效率比较低
     */
    public String getIPA(NamPinyin p, String dict)
    {
        if (!p.isValid()) return UniPinyin.getError();
        String ans = IPATool.merge(
                Yinjie.of(m.findByPinyin(p.getPinyin())),
                Shengdiao.of(m.findByTone(p.getTone())),
                dict);
        return ans == null ? UniPinyin.getError() : ans;
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
    public Set<String> getDictonarySet()
    {
        return Yinjie.of(m.findByPinyin("ba")).getInfo().keySet();
    }


    /**
     * 传入一条拼音，把所有字典版本的IPA全部转换出来
     *
     * @apiNote 只有两次查询，是最高效的版本
     */
    public Map<String, String> getAllIPA(NamPinyin p)
    {
        return getMultiLine(Set.of(p)).getOrDefault(p, Map.of());
    }

    /**
     * 传入多条拼音，把所有字典版本的IPA全部转换出来
     *
     * @return 一个拼音的结果全部无效，就不会出现在结果Map里
     * @apiNote 只有两次查询，是最高效的版本
     */
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
        Map<String, IPASyllableEntity> syllableMap = new HashMap<>();
        Map<Integer, IPAToneEntity> toneMap = new HashMap<>();

        //如果查询的为空，那么会异常，接受异常后直接返回空集合
        try
        {
            for (var i : m.findAllPinyinList(syllable))
                syllableMap.put(i.getStandard(), i);
            for (var i : m.findAllToneList(tone))
                toneMap.put(i.getStandard(), i);
        } catch (Exception e)
        {
            return Map.of();
        }

        for (var pinyin : p)
        {
            if (pinyin.isValid())
            {
                Map<String, String> tmp = new HashMap<>();
                Boolean allNull = true;
                for (var dict : getDictonarySet())
                {
                    String ans = IPATool.merge(
                            Yinjie.of(syllableMap.get(pinyin.getPinyin())),
                            Shengdiao.of(toneMap.get(pinyin.getTone())),
                            dict);
                    tmp.put(dict, ans == null ? UniPinyin.getError() : ans);
                    if (ans != null) allNull = false;
                }
                if (!allNull) map.put(pinyin, tmp);
            }
        }
        return map;
    }

    protected List<YinjiePart> getConstructData(List<String> str)
    {
        List<YinjiePart> res = new ArrayList<>();
        for (var i : m.findAllElementList(str)) res.add(YinjiePart.of(i));
        return res;
    }

    public Yinjie constructSyllable(NamPinyin p)
    {
        return IPATool.constructIPA(p, this::getConstructData);
    }

    public void insertSyllable(NamPinyin p)
    {
        // 如果拼音无效，或者已经产生了数据
        if (!p.isValid()) return;
        if (!getAllIPA(p).isEmpty()) return;

        Yinjie s = constructSyllable(p);
        m.insertPinyin(s.transfer());
    }

    /**
     * 测试
     */
    public Pair<Map<String, Integer>, Set<String>> check()
    {
        int success = 0, fail = 0;
        Set<String> failCase = new HashSet<>();

        Set<Yinjie> a = new HashSet<>();
        Map<String, YinjiePart> b = new HashMap<>();
        for (var i : m.findAllPinyin()) a.add(Yinjie.of(i));
        for (var i : m.findAllElement())
            b.put(YinjiePart.of(i).getCode(), YinjiePart.of(i));


        for (var i : a)
        {
            var merge = IPATool.constructIPA(
                    NamPinyin.of(i.getPinyin()),
                    (List<String> str) ->
                    {
                        List<YinjiePart> res = new ArrayList<>();
                        res.add(b.get(str.get(0)));
                        res.add(b.get(str.get(1)));
                        return res;
                    });

            if (i.equals(merge)) success++;
            else
            {
                fail++;
                failCase.add(i.getPinyin());
                System.out.println(merge);
                System.out.println(i);
                System.out.println();
            }
        }
        return Pair.of(
                Map.of("success", success, "fail", fail),
                failCase
        );
    }

    /**
     * 传入风格，返回这个风格的诗歌的示例（暂时只有这一首）
     */
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
