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
    private final List<List<Hanzi>> list; // 内层list：同一个字的多个内容，外层list：多个汉字
    private final Language language;

    public HanziEntry(List<CharEntity> ch, Language language)
    {
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
        Map<String, List<Hanzi>> ans = new HashMap<>();
        for (CharEntity item : ch)
        {
            Hanzi i = Hanzi.of(item, language);
            String key = switch (language)
            {
                case SC -> i.getHanzi();
                case TC -> i.getHantz();
            };

            ans.computeIfAbsent(key, k -> new ArrayList<>());
            ans.get(key).add(i);
        }
        list = new ArrayList<>(ans.values());
        this.language = language;
    }

    public static HanziEntry of(List<CharEntity> ch, Language language)
    {
        return new HanziEntry(ch, language);
    }
}
