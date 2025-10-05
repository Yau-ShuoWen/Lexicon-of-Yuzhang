package com.shuowen.yuzong.dao.domain.Character;

import com.shuowen.yuzong.Tool.dataStructure.Language;
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
    Language language = Language.CH;

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

    protected void add(T hz, Language lang)
    {
        /* 在加入的时候不能简体繁体结果集混合
         * 1. 如果结果集是空的，那可以随便加上
         * 2. 如果两个语言枚举相等，也可以加上
         * 3. 往非空而且语言枚举不同的结果集里加入报错
         * */
        if (isEmpty()) language = lang;
        else
        {
            if (!Objects.equals(lang, language))
                throw new IllegalArgumentException("简体繁体互相混合");
        }


        // 对内容区分简繁体的字段确定个版本（避免传输消耗）
        if (!lang.isCH())
        {
            String l = lang.toString();
            String r = lang.reverse().toString();

            // 直接留下最外层的目标路径即可
            hz.setPyExplain(Map.of(l, hz.getPyExplain().get(l)));
            hz.setMean(Map.of(l, hz.getMean().get(l)));
            hz.setNote(Map.of(l, hz.getNote().get(l)));
            hz.setRefer(Map.of(l, hz.getRefer().get(l)));

            // 删掉相反的路径即可
            for (var i : (Collection<Map<String, String>>) hz.getMulPy().values())
                i.remove(r);

            for (var i : (Collection<Map<String, String>>) hz.getIpaExp())
                i.remove(r);
        }
        list.add(hz);
    }

    /**
     * 分裂函数，按照简体字或者繁体字把他分成若干个结果集，每一个结果集里，对应语言同一个字
     * */
    public List<HanziEntry<T>> split(Language l)
    {
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
            String key = switch (l)
            {
                case SC -> i.getHanzi();
                case TC -> i.getHantz();
                case CH -> i.getHanzi() + i.getHantz();
            };

            if (!ans.containsKey(key))
                ans.put(key, new HanziEntry<>(this.factory));
            ans.get(key).add(i, l);
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
