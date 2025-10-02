package com.shuowen.yuzong.dao.domain.Character;

import com.shuowen.yuzong.dao.model.Character.CharEntity;
import lombok.Data;

import java.util.*;
import java.util.function.Function;

/**
 * 汉字结果集
 * */

@Data
public class HanziEntry<T extends Hanzi>
{
    List<T> list = new ArrayList<>();
    String language = "";

    // 工厂函数：用于创建T类型的Hanzi对象
    private final Function<CharEntity, T> factory;

    /**
     * 空构造
     */
    public HanziEntry(Function<CharEntity, T> factory)
    {
        this.factory = factory;
    }

    /**
     * 结果集是List<CharEntity>，所以构造函数直接使用
     * */
    public HanziEntry(List<CharEntity> entity, Function<CharEntity, T> factory)
    {
        this.factory = factory;
        for (CharEntity nc : entity)
        {
            T hanzi = factory.apply(nc);
            list.add(hanzi);
        }
    }


    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    protected void testLangValid(String lang)
    {
        if (!"tc".equals(lang) && !"sc".equals(lang))
            throw new IllegalArgumentException("语言不正确");
    }

    protected void add(T nh, String lang)
    {
        testLangValid(lang);

        // 当已经是繁体字了，加入的是简体，或者反之，则异常
        // 如果language是""，说明还未加入，不报错
        if ("tc".equals(lang) && "sc".equals(language) || "sc".equals(lang) && "tc".equals(language))
        {
            throw new IllegalArgumentException("简体繁体互相混合");
        }

        language = lang;// 之前检查了内容不相同，现在这句是为了对未加入内容处理

        // 对内容区分简繁体的字段确定个版本（避免传输消耗）
        nh.setPyExplain(Map.of(lang, nh.getPyExplain().get(lang)));
        nh.setMean(Map.of(lang, nh.getMean().get(lang)));
        nh.setNote(Map.of(lang, nh.getNote().get(lang)));
        nh.setRefer(Map.of(lang, nh.getRefer().get(lang)));
        list.add(nh);
    }

    /**
     * 分裂函数，按照简体字或者繁体字把他分成若干个结果集，每一个结果集里，对应语言同一个字
     * */
    public List<HanziEntry<T>> split(String lang)
    {
        testLangValid(lang);

        Map<String, HanziEntry<T>> ans = new HashMap<>();

        /*
         * 遍历结果集里的汉字，按照什么聚合根据语言决定
         *
         * 意思：
         * 1. 比如要按照简体聚合，那么我们不关心“斗”“鬥”两个字作为繁体字的区别，
         * 我们认为是同一个简体字“斗”的不同含义，这时候聚合成为一个结果集，所以
         * 在hashmap里用简体作为key，相同就可以集合
         *
         * 2. 比如要按照繁体聚合，那么我们不关心“斗”“鬥”简体字相同的情况，我们认
         * 为就是两个不同的词条，这时候应该分化为两个结果集，所以在hashmap里使用
         * 繁体作为key
         *
         * 如果是第一次出现记录：创建新的结果集。如果是之后加入的，合并
         * */
        for (T i : list)
        {
            String key = "sc".equals(lang) ? i.getHanzi() : i.getHantz();

            if (!ans.containsKey(key))
                ans.put(key, new HanziEntry<>(this.factory));
            ans.get(key).add(i, lang);
        }
        return new ArrayList<>(ans.values());
    }

    /**
     * 静态工厂方法 - 创建指定类型的HanziEntry
     */
    public static <T extends Hanzi> HanziEntry<T> of(
            List<CharEntity> charEntities,
            Function<CharEntity, T> factory)
    {
        return new HanziEntry<>(charEntities, factory);
    }

    /**
     * 创建空的HanziEntry
     */
    public static <T extends Hanzi> HanziEntry<T> of(
            Function<CharEntity, T> factory)
    {
        return new HanziEntry<>(factory);
    }

}
