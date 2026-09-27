package com.shuowen.yuzong.linguistics.pinyinv2;

import com.shuowen.yuzong.linguistics.pinyinv2.lac.*;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.tuple.Maybe;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 辞书身份与方言归属的唯一注册处。
 * <p>
 * 注册表只负责找到辞书，不参与任何语言学计算。
 */
public final class DictionaryRegistry
{
    private static final Map<DictCode, PronunciationDictionary> IPA_DICTIONARIES = Map.of(
            DictCode.STANDARD, Dict_Standard.INSTANCE,
            DictCode.NCDICT, Dict_NanchangFangyanCidian.INSTANCE,
            DictCode.NCSTUDY, Dict_NanchangFangyanYanjiu.INSTANCE,
            DictCode.NCPHON, Dict_NanchangYinxi.INSTANCE,
            DictCode.NCPHON_Y, Dict_NanchangYinxiYanshiBiaoyin.INSTANCE,
            DictCode.NCRECORD, Dict_NanchanghuaYindang.INSTANCE,
            DictCode.CNDIALDICT, Dict_HanyuFangyinZihui.INSTANCE,
            DictCode.GANSUM, Dict_GanFangyanGaiyao.INSTANCE
    );

    static
    {
        for (Dialect dialect : Dialect.values())
        {
            List<DictCode> fallback = codesOf(dialect, DictCode::isFallback);
            if (fallback.size() != 1)
                throw new IllegalStateException(dialect + "必须且只能有一个默认 IPA 方案：" + fallback);
            if (!IPA_DICTIONARIES.containsKey(fallback.get(0)))
                throw new IllegalStateException(dialect + "的默认 IPA 方案没有注册实现：" + fallback.get(0));
        }

        for (DictCode code : DictCode.values())
            if (code.derived() && (!code.centralDictionary().isCompleteDictionary() ||
                    code.centralDictionary().getDialect() != code.getDialect()))
                throw new IllegalStateException(code + "的中心辞书设置无效：" + code.centralDictionary());
    }

    private DictionaryRegistry()
    {
    }

    public static List<DictCode> dictionariesOf(Dialect dialect)
    {
        return codesOf(dialect, DictCode::isCompleteDictionary);
    }

    /**
     * 可作为独立 IPA 版本展示的项目。包含派生记音，不包含内部兜底方案。
     */
    public static List<DictCode> ipaVersionsOf(Dialect dialect)
    {
        return codesOf(dialect, code -> !code.isFallback() && IPA_DICTIONARIES.containsKey(code));
    }

    /**
     * 一部中心辞书可以提供的全部 IPA 版本。传入派生版本时也会先回到中心辞书。
     */
    public static List<DictCode> ipaVersionsOf(DictCode dictionary)
    {
        DictCode central = dictionary.centralDictionary();
        return ipaVersionsOf(central.getDialect()).stream()
                .filter(code -> code.centralDictionary() == central)
                .toList();
    }

    public static Maybe<PronunciationDictionary> defaultIpaDictionary(Dialect dialect)
    {
        DictCode code = codesOf(dialect, DictCode::isFallback).get(0);
        return findIpaDictionary(code);
    }

    public static Maybe<PronunciationDictionary> findIpaDictionary(DictCode code)
    {
        return Maybe.uncertain(IPA_DICTIONARIES.get(code));
    }

    public static boolean belongsTo(DictCode code, Dialect dialect)
    {
        return code.getDialect() == dialect;
    }

    private static List<DictCode> codesOf(Dialect dialect, Predicate<DictCode> filter)
    {
        return Arrays.stream(DictCode.values())
                .filter(code -> code.getDialect() == dialect)
                .filter(filter)
                .toList();
    }
}
