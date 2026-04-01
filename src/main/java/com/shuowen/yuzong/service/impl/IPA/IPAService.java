package com.shuowen.yuzong.service.impl.IPA;

import com.shuowen.yuzong.Linguistics.Scheme.Pinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.SetTool;
import com.shuowen.yuzong.Tool.TestTool.EqualChecker;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
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
     * 传入多条拼音，把所有字典版本的IPA全部转换出来
     */
    public Map<Pinyin, Map<DictCode, String>> getIPA(
            Set<Pinyin> pinyinSet, PinyinOption op, Dialect d, Set<DictCode> dictSet)
    {
        // 给出的是拼音的set，通过mapping获得对应拼音/音调的set
        // 这个set查数据库，查询之后获得的是对应数据条目的set
        // 按照standard为关键字映射成map可以查询

        var syll = Yinjie.mapOf(
                m.findSyllableListByStandard(
                        SetTool.mapping(pinyinSet, Pinyin::getSyll),
                        d.toString())
        );
        var tone = Shengdiao.mapOf(
                m.findToneInfoSet(
                        SetTool.mapping(pinyinSet, Pinyin::getTone),
                        d.toString())
        );

        Map<Pinyin, Map<DictCode, String>> dataPerPinyin = new HashMap<>();

        for (var pinyin : pinyinSet)
        {
            var y = syll.get(pinyin.getSyll());
            var s = tone.get(pinyin.getTone());
            if (y == null || s == null) continue;

            Map<DictCode, String> dataPerDict = new HashMap<>();
            for (var dict : dictSet)
            {
                // 如果查不到结果，那么对于这个字典的这个读音就是无效的，直接略过
                var yj = y.getInfo(dict);
                var sd = s.getInfo(dict);
                if (yj == null || sd == null) continue;

                var tmp = switch (op.getTone())
                {
                    case FIVE_DEGREE_NUM -> IPATool.mergeFiveDegree(yj, sd, true);
                    case FIVE_DEGREE_LINE -> IPATool.mergeFiveDegree(yj, sd, false);
                    case FOUR_CORNER -> IPATool.mergeFourCorner(yj, pinyin.getCorner());
                };
                dataPerDict.put(dict, IPATool.formatSyllable(tmp, op.getSyllable()));
            }

            dataPerPinyin.put(pinyin, dataPerDict);
        }
        return dataPerPinyin;
    }

    /**
     *
     */
    public EqualChecker<Yinjie> checkIPA(Dialect d)
    {
        EqualChecker<Yinjie> checker = new EqualChecker<>();

        var map = Shengyun.mapOf(m.getAllSegment(d.toString()));  // 获得查询资料

        for (var i : Yinjie.listOf(m.getAllSyllable(d.toString())))
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
    public void insertSyllable(Pinyin p, Dialect d)
    {
        if (m.findSyllableByStandard(p.getSyll(), d.toString()) != null) return;
        var merge = constructIPA(d, p, i -> Shengyun.tryOf(m.findSegmentInfo(i, d.toString())));
        if (merge.isValid()) m.insertSyllable(merge.getValue().transfer(), d.toString());
    }

    /**
     * @param data 查询可能找得到也可能找不到的函数，找不到会在Yinjie.merge()里返回安全空
     */
    private Maybe<Yinjie> constructIPA(
            Dialect d, Pinyin pinyin, Function<String, Maybe<Shengyun>> data)
    {
        String code = pinyin.getCode();

        int left = d.getInitialLength();
        int right = code.length() - left;

        String initial = code.substring(0, left) + "~".repeat(right);
        String last = "~".repeat(left) + code.substring(left);

        return Yinjie.merge(data.apply(initial), data.apply(last));
    }

    private static IPAService instance;

    @PostConstruct
    public void init()
    {
        instance = this;
    }

    public static Map<Pinyin, Map<DictCode, String>> getTheIPA(
            Set<Pinyin> pinyinSet, PinyinOption op, Dialect d, Set<DictCode> dictSet)
    {
        return instance.getIPA(pinyinSet, op, d, dictSet);
    }

    public static List<IPAItem> getTableItem(Dialect d, String key)
    {
        return instance.m.getTableItem(d.toString(),key);
    }
}
