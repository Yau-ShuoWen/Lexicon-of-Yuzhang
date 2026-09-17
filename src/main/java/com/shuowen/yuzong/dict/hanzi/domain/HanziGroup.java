package com.shuowen.yuzong.dict.hanzi.domain;

import com.shuowen.yuzong.dict.hanzi.model.HanziEntity;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.UChar;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class HanziGroup
{
    private final UChar hanzi;
    private final List<HanziItem> data;
    private final Dialect dialect;

    public static List<HanziGroup> listOf(List<HanziEntity> entities, Language language, Dialect dialect)
    {
        Map<UChar, List<HanziItem>> grouped = new LinkedHashMap<>();
        for (var entity : entities)
        {
            var item = new HanziItem(entity, language);
            grouped.computeIfAbsent(item.getHanzi(), key -> new ArrayList<>()).add(item);
        }
        return grouped.entrySet().stream()
                .map(i -> new HanziGroup(i.getKey(), i.getValue(), dialect)).toList();
    }

    public String getPinyin()
    {
        return data.stream().flatMap(i -> i.getPinyin().stream())
                .map(i -> dialect.trustedCreatePinyin(i.getPinyin().toString()).toRPinyin().toString())
                .distinct().reduce((a, b) -> a + "/" + b).orElse("");
    }

    public boolean isSpecial()
    {
        return data.stream().anyMatch(i -> i.getSpecial() != 0);
    }
}
