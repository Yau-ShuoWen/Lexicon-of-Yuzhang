package com.shuowen.yuzong.service.impl.IPA;

import com.shuowen.yuzong.Linguistics.Scheme.Pinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.MapTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.SetTool;
import com.shuowen.yuzong.Tool.TestTool.EqualChecker;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.data.domain.IPA.*;
import com.shuowen.yuzong.data.mapper.IPA.IPAMapper;
import com.shuowen.yuzong.data.model.IPA.IPASyllableEntity;
import com.shuowen.yuzong.data.model.IPA.IPAToneEntity;
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
     *
     * @apiNote 如果只有一条拼音要处理，请用 {@code Set.of()}包裹起来
     */
    public Map<Pinyin, Map<String, String>> getIPA(Set<Pinyin> pinyinSet, PinyinOption op, Dialect d, Set<String> dictSet)
    {
        // 从数组里获取拼音/声调的set、查询之后获得的是对应数据条目的set
        var syllableData = m.findSyllableListByStandard(SetTool.mapping(pinyinSet, Pinyin::getPinyin), d.toString());
        var toneData = m.findToneListByTone(SetTool.mapping(pinyinSet, Pinyin::getTone), d.toString());

        // 按照standard为关键字映射成map可以查询
        var syllable = MapTool.fromSet(syllableData, IPASyllableEntity::getStandard);
        var tone = MapTool.fromSet(toneData, IPAToneEntity::getStandard);

        Map<Pinyin, Map<String, String>> map = new HashMap<>();
        for (var pinyin : pinyinSet)
        {
            map.put(pinyin, IPATool.mergeAPI(
                    Yinjie.tryOf(syllable.get(pinyin.getPinyin())),  // 音节的查询结果
                    Shengdiao.tryOf(tone.get(pinyin.getTone())),     // 音调的查询结果
                    pinyin, op, dictSet                              // 拼音本身 参数 字典
            ));
        }
        return map;
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
            var pinyinAnswer = d.tryCreatePinyin(i.getPinyin());
            var merge = constructIPA(pinyinAnswer, a -> Maybe.uncertain(map.get(a)));
            checker.maybeCheck(i, merge);
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
        if (m.findSyllableByStandard(p.getPinyin(), d.toString()) != null) return;
        var merge = constructIPA(Maybe.exist(p), i -> Shengyun.tryOf(m.findSegmentByCode(i, d.toString())));
        if (merge.isValid()) m.insertSyllable(merge.getValue().transfer(), d.toString());
    }

    /**
     * @param pinyinMaybe 可能有效也可能无效的拼音，无效就直接返回安全空
     * @param data        查询可能找得到也可能找不到的函数，找不到会在Yinjie.merge()里返回安全空
     */
    private static Maybe<Yinjie> constructIPA
    (Maybe<? extends Pinyin> pinyinMaybe, Function<String, Maybe<Shengyun>> data)
    {
        if (pinyinMaybe.isEmpty()) return Maybe.nothing();
        var pinyin = pinyinMaybe.getValue();

        String code = pinyin.getCode();

        int left = pinyin.getInitialLen();
        int right = code.length() - left;

        String initial = code.substring(0, left) + "~".repeat(right);
        String last = "~".repeat(left) + code.substring(left);

        return Yinjie.merge(data.apply(initial), data.apply(last));
    }
}
