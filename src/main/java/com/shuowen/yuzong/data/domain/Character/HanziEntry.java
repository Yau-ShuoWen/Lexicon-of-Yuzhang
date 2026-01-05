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

    public HanziEntry(List<CharEntity> ch, Language language)
    {
        Map<String, List<Hanzi>> ans = new HashMap<>();
        for (CharEntity item : ch)
        {
            Hanzi i = Hanzi.of(item, language);
            // 根据汉字聚合结果
            String key = i.getTheHanzi();

            ans.computeIfAbsent(key, k -> new ArrayList<>());
            ans.get(key).add(i);
        }
        list = new ArrayList<>(ans.values());
    }

    public static HanziEntry of(List<CharEntity> ch, Language language)
    {
        return new HanziEntry(ch, language);
    }
}
