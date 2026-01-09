package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import lombok.Data;

import java.util.*;

/**
 * 汉字结果集
 */
@Data
public class HanziGroup
{
    private final List<List<HanziItem>> list; // 内层list：同一个字的多个内容，外层list：多个汉字

    public HanziGroup(List<HanziEntity> ch, Language language)
    {
        Map<String, List<HanziItem>> ans = new HashMap<>();
        for (HanziEntity i : ch)
        {
            HanziItem item = HanziItem.of(i, language);

            // 根据汉字聚合结果
            String key = item.getHanzi();
            ans.computeIfAbsent(key, k -> new ArrayList<>());
            ans.get(key).add(item);
        }
        list = new ArrayList<>(ans.values());
    }

    public static HanziGroup of(List<HanziEntity> ch, Language language)
    {
        return new HanziGroup(ch, language);
    }
}
