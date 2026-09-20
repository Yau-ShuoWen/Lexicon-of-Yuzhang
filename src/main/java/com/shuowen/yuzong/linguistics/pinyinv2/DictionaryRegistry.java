package com.shuowen.yuzong.linguistics.pinyinv2;

import com.shuowen.yuzong.linguistics.pinyinv2.lac.*;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.tuple.Maybe;

import java.util.List;
import java.util.Map;

/**
 * 辞书身份与方言归属的唯一注册处。
 * <p>
 * 注册表只负责找到辞书，不参与任何语言学计算。
 */
public final class DictionaryRegistry
{
    private static final Map<DictCode, PronunciationDictionary> IPA_DICTIONARIES = Map.of(
            DictCode.NCDICT, Dict_NanchangFangyanCidian.INSTANCE,
            DictCode.NCSTUDY, Dict_NanchangFangyanYanjiu.INSTANCE,
            DictCode.NCPHON, Dict_NanchangYinxi.INSTANCE,
            DictCode.NCPHON_Y, Dict_NanchangYinxiYanshiBiaoyin.INSTANCE,
            DictCode.NCRECORD, Dict_NanchanghuaYindang.INSTANCE,
            DictCode.CNDIALDICT, Dict_HanyuFangyinZihui.INSTANCE,
            DictCode.GANSUM, Dict_GanFangyanGaiyao.INSTANCE
    );

    private static final Map<Dialect, List<DictCode>> DICTIONARIES_BY_DIALECT = Map.of(
            Dialect.LAC, List.of(
                    DictCode.NCDICT,
                    DictCode.NCSTUDY,
                    DictCode.NCPHON,
                    DictCode.NCPHON_Y,
                    DictCode.NCRECORD,
                    DictCode.NCDIALSTD,
                    DictCode.CNDIALDICT,
                    DictCode.GANSUM
            )
    );

    private DictionaryRegistry()
    {
    }

    public static List<DictCode> dictionariesOf(Dialect dialect)
    {
        return DICTIONARIES_BY_DIALECT.getOrDefault(dialect, List.of());
    }

    public static Maybe<PronunciationDictionary> findIpaDictionary(DictCode code)
    {
        return Maybe.uncertain(IPA_DICTIONARIES.get(code));
    }

    public static boolean belongsTo(DictCode code, Dialect dialect)
    {
        return dictionariesOf(dialect).contains(code);
    }
}
