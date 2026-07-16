package com.shuowen.yuzong.service.impl.IPA;

import com.shuowen.yuzong.Linguistics.IPA.IPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.util.test.EqualChecker;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.util.tuple.Quadruple;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.DictGroup;
import com.shuowen.yuzong.data.mapper.IPA.IPAMapper;
import com.shuowen.yuzong.data.model.IPA.IPAItem;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

/**
 * 国际音标查询类
 *
 * @implSpec 这里不保证传参的拼音和方言代号是对应的，请在调用的时候检查
 */

@Service
public class IPAService
{
    @Autowired
    private IPAMapper m;

    /**
     *
     */
    public EqualChecker<Yinjie> checkIPA(Dialect d)
    {
        EqualChecker<Yinjie> checker = new EqualChecker<>();

        var map = Shengyun.mapOf(m.getAllSegment(d.toString()));  // 获得查询资料

        for (var i : Yinjie.listOf(m.getAllSyll(d.toString())))
        {
            var pinyinAnswer = d.trustedCreatePinyin(i.getPinyin());
            var merge = constructIPA(d, pinyinAnswer, a -> Maybe.uncertain(map.get(a)));
            checker.check(i, merge);
        }
        return checker;
    }

    /**
     *
     */
    public void updateIPA(Dialect d)
    {
        // 直接获得所有查询的不一样内容，只要不是因为构造失败而被记录的（maybeCheck的流程），就以组合的为准来更新
        var answer = checkIPA(d);
        if (answer.allTrue()) System.out.println("没有需要更新的数据");
        else
        {
            System.out.println("以下数据要更新");
            answer.report();
            for (var i : answer.getLog())
            {
                if (i.getRight() != null) m.changeSyllableInfo(i.getRight().transfer(), d.toString());
            }
            System.out.println("更新完成");
        }
    }

    /**
     *
     */
    public void insertSyllable(IPinyin p, Dialect d)
    {
        if (m.findSyllableByStandard(p.getSyll(), d.toString()) != null) return;
        var merge = constructIPA(d, p, i -> Shengyun.tryOf(m.findSegmentInfo(i, d.toString())));
        if (merge.isValid()) m.insertSyllable(merge.getValue().transfer(), d.toString());
    }

    /**
     * @param data 查询可能找得到也可能找不到的函数，找不到会在Yinjie.merge()里返回安全空
     */
    private Maybe<Yinjie> constructIPA(
            Dialect d, IPinyin pinyin, Function<String, Maybe<Shengyun>> data)
    {
        String code = pinyin.getCode();

        int left = d.getInitialLength();
        int right = code.length() - left;

        String initial = code.substring(0, left) + "~".repeat(right);
        String last = "~".repeat(left) + code.substring(left);

        return Yinjie.merge(data.apply(initial), data.apply(last));
    }

    //=========================================

    private static IPAService instance;

    @PostConstruct
    public void init()
    {
        instance = this;
    }

    public static List<IPAItem> getTableItem(Dialect d, String key)
    {
        return instance.m.getTableItem(d.toString(), key);
    }

    public static Map<Quadruple<IPinyin, DictCode, IPASyllStyle, IPAToneStyle>, String> getData(Dialect d)
    {
        var syll = instance.m.getAllSyll(d.toString());
        var tone = instance.m.getAllTone(d.toString());

        Map<Quadruple<IPinyin, DictCode, IPASyllStyle, IPAToneStyle>, String> data = new LinkedHashMap<>();

        // 单拼音，无音调 =================

        for (var s : syll)
        {
            var yj = Yinjie.of(s);
            var pinyin = d.trustedCreatePinyin(SPinyin.of(s.getStandard()));
            for (var dict : DictGroup.of(d).getKeySet())
            {
                for (var sy : IPASyllStyle.values())
                {
                    for (var to : IPAToneStyle.values())
                    {
                        var ipaSyll = yj.getInfo(dict);
                        if (ipaSyll == null) continue;
                        var ipa = IPAFormatter.formatSyllable(ipaSyll, sy);
                        data.put(Quadruple.of(pinyin, dict, sy, to), ipa);
                    }
                }
            }
        }

        // 有音调 ===============================

        for (var s : syll)
        {
            var yj = Yinjie.of(s);
            for (var t : tone)
            {
                var sd = Shengdiao.of(t);
                try
                {
                    var pinyin = d.trustedCreatePinyin(SPinyin.of(s.getStandard()+t.getStandard()));
                    for (var dict : DictGroup.of(d).getKeySet())
                    {
                        for (var sy : IPASyllStyle.values())
                        {
                            for (var to : IPAToneStyle.values())
                            {
                                var syllStr = yj.getInfo(dict);
                                var toneStr = sd.getInfo(dict);
                                if (syllStr == null || toneStr == null) continue;

                                var ipa = switch (to)
                                {
                                    case FIVE_DEGREE_NUM -> IPAFormatter.mergeFiveDegree(syllStr, toneStr, true);
                                    case FIVE_DEGREE_LINE -> IPAFormatter.mergeFiveDegree(syllStr, toneStr, false);
                                    case FOUR_CORNER -> IPAFormatter.mergeFourCorner(syllStr, pinyin.getCorner());
                                };
                                ipa = IPAFormatter.formatSyllable(ipa, sy);
                                data.put(Quadruple.of(pinyin, dict, sy, to), ipa);
                            }
                        }
                    }
                } catch (IllegalArgumentException ignored)
                {
                }
            }
        }
        return data;
    }
}
