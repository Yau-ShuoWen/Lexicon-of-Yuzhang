package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.model.Character.CharEntity;
import lombok.Data;

import java.util.*;


/**
 * 汉字结果集
 */

@Data
public class HanziEntry
{
    List<Hanzi> list = new ArrayList<>();
    Language language = Language.CH;

    public HanziEntry() {}

    /**
     * 结果集是List<CharEntity>，所以构造函数直接使用
     */
    public HanziEntry(List<CharEntity> entity)
    {
        for (CharEntity i : entity) list.add(Hanzi.of(i));
    }

    public static HanziEntry of()
    {
        return new HanziEntry();
    }

    public static HanziEntry of(List<CharEntity> charEntities)
    {
        return new HanziEntry(charEntities);
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    protected void add(Hanzi hz, Language lang)
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

        hz.changeLang(language);
        list.add(hz);
    }

    /**
     * 分裂函数，按照简体字或者繁体字把他分成若干个结果集，每一个结果集里，对应语言同一个字
     */
    public List<HanziEntry> split(Language l)
    {
        Map<String, HanziEntry> ans = new HashMap<>();

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
        for (Hanzi i : list)
        {
            String key = switch (l)
            {
                case SC -> i.getHanzi();
                case TC -> i.getHantz();
                case CH -> i.getHanzi() + i.getHantz();
            };

            if (!ans.containsKey(key)) ans.put(key, new HanziEntry());
            ans.get(key).add(i, l);
        }
        return new ArrayList<>(ans.values());
    }

    public Hanzi getItem(int i)
    {
        return list.get(i);
    }
}
