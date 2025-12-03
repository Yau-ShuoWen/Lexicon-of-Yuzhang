package com.shuowen.yuzong.service.impl.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.mapper.IPA.IPAMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;

import java.util.*;
import java.util.function.Function;

import static com.shuowen.yuzong.data.domain.Pinyin.PinyinTool.parseAndReplace;

@Service
public class PinyinService
{
    @Autowired
    private IPAMapper m;

    @SuppressWarnings ("unchecked")
    public <U extends PinyinStyle> U getStandardStyle(Dialect d)
    {
        if(!d.isValid()) throw new RuntimeException("无效方言代码");
        return (U) d.styleSupplier.get();
    }


    public <U extends PinyinStyle, T extends UniPinyin<U>>
    Function<String, T> getFactory(Dialect d)
    {
        if(!d.isValid()) throw new RuntimeException("无效方言代码");
        return d.getFactory();
    }

    public String getDefaultDict(Dialect d)
    {
        return switch (d)
        {
            case NAM -> "ncdict";
            default -> throw new RuntimeException("无效方言代码");
        };
    }

    /**
     * 获得目标拼音「音节」的所有字典的IPA版本
     */
    public <U extends PinyinStyle, T extends UniPinyin<U>>
    Map<String, String> getAllIPASyllable(T p, Dialect d)
    {
        if (!p.isValid()) return Map.of();
        return Yinjie.of(m.findByPinyin(p.getPinyin(), d.toString())).getInfo();
    }

    /**
     * 获得目标拼音「音调」的所有字典的IPA版本
     */
    public <U extends PinyinStyle, T extends UniPinyin<U>>
    Map<String, String> getAllIPATone(T p, Dialect d)
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
    public <U extends PinyinStyle, T extends UniPinyin<U>>
    Map<String, String> getAllIPA
    (T p, IPAToneStyle ts, IPASyllableStyle ss, Dialect d)
    {
        return getMultiLine(Set.of(p), ts, ss, d).getOrDefault(p, Map.of());
    }


    /**
     * 传入多条拼音，把所有字典版本的IPA全部转换出来
     *
     * @apiNote 只有两次查询，是最高效的版本
     * @see IPATool
     */
    public <U extends PinyinStyle, T extends UniPinyin<U>>
    Map<T, Map<String, String>> getMultiLine
    (Set<T> p, IPAToneStyle ts, IPASyllableStyle ss, Dialect d)
    {
        return IPATool.getMultiline(p, ts, ss, getDictionarySet(d), m::findAllPinyinList, m::findAllToneList, d);
    }

    public <U extends PinyinStyle, T extends UniPinyin<U>>
    void insertSyllable(T p, Dialect d)
    {
        // 如果拼音无效，或者已经产生了数据
        if (!p.isValid()) return;
        if (!getAllIPA(p, IPAToneStyle.FIVE_DEGREE_LINE, IPASyllableStyle.CHINESE_SPECIAL, d).isEmpty()) return;

        IPATool.insertSyllable(m::findElement, m::insertPinyin,p,  d);
    }

    /**
     * 测试
     */
    public Pair<Map<String, Integer>, Set<String>> check(Dialect d)
    {
        return IPATool.checkIPA(m::findAllPinyin, m::findAllElement, d.getFactory(), d);
    }

    public void updateIPA(Dialect d)
    {
        int num = check(d).getRight().size();
        if (num == 0)
        {
            System.out.println("没有需要更新的数据");
            return;
        }
        System.out.println("开始更新 " + num + " 条数据");
        // 执行更新
        IPATool.updateIPA(m::findAllPinyin, m::findAllElement, d.getFactory(), m::changeInfo, d);
        System.out.println("更新完成");
    }


    /**
     * 传入风格，返回这个风格的诗歌的示例
     */
    public String getPreview(PinyinStyle style, Dialect d)
    {
        switch (d)
        {
            case NAM ->
            {
                String s1 = "枫桥夜泊\n月落乌啼霜满天，江枫渔火对愁眠。\n姑苏城外寒山寺，夜半钟声到客船。\n";
                String p1 = """
                        [fung1][qieu2][ia5][pok6]
                        [nvot6][lok7][u1][ti2][song1][mon3][tien1]
                        [gong1][fung1][v4][fo3][dui4][ceu2][mien4]
                        [gu1][su1][ceen2][uai5][hon2][san1][sii5]
                        [ia5][bon4][zung1][sen1][tau5][kak6][cuon2]
                        """;

                String s2 = "山行\n远上寒山石径斜，白云生处有人家。\n停车坐爱枫林晚，霜叶红于二月花。\n";
                String p2 = """
                        [san1][xin4]
                        [von2][song5][hon2][san1][sak7][jin4][xia4]
                        [pak6][vn4][sang1][cu5][iu3][nin4][ga1]
                        [tiang2][ca1][co5][ngai4][fung1][lin4][uon3]
                        [suong1][iet6][fung4][v4][oe5][nvot6][fa1]
                        """;

                String s3 = "江雪\n千山鸟飞绝，万径人踪灭。\n孤舟蓑笠翁，独钓寒江雪。\n";
                String p3 = """
                        [gong1][xuot6]
                        [qien1][san1][nieu3][feei1][jvot6]
                        [uon5][jin4][nin4][zung1][miet6]
                        [gu1][zeu1][suo1][lit7][ung4]
                        [tuk7][dieu4][hon2][gong1][xvot6]
                        """;

                return s1 + parseAndReplace(p1, NamPinyin::new, (NamStyle) style, "[", "]") + "\n" +
                        s2 + parseAndReplace(p2, NamPinyin::new, (NamStyle) style, "[", "]") + "\n" +
                        s3 + parseAndReplace(p3, NamPinyin::new, (NamStyle) style, "[", "]");
            }

            default -> throw new RuntimeException("不存在的方言");
        }
    }
}
