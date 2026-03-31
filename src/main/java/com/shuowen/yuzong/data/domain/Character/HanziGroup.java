package com.shuowen.yuzong.data.domain.Character;

import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.SetTool;
import com.shuowen.yuzong.Tool.dataStructure.UChar;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Linguistics.Scheme.PinyinFormatter;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import lombok.Data;

import java.util.*;

/**
 * 汉字结果集
 */
@Data
public class HanziGroup
{
    private final UChar hanzi;
    private final List<HanziItem> data;
    private final Dialect dialect;

    private HanziGroup(UChar hanzi, List<HanziItem> data, Dialect dialect)
    {
        this.hanzi = hanzi;
        this.data = data;
        this.dialect = dialect;
    }

    public static List<HanziGroup> listOf(List<HanziEntity> ch, Language language, Dialect d)
    {
        Map<UChar, List<HanziItem>> ans = new HashMap<>();
        for (HanziEntity i : ch)
        {
            HanziItem item = new HanziItem(i, language);
            // 根据汉字聚合结果
            UChar key = item.getHanzi();
            ans.computeIfAbsent(key, k -> new ArrayList<>());
            ans.get(key).add(item);
        }

        List<HanziGroup> groups = new ArrayList<>();
        for (var i : ans.entrySet()) groups.add(new HanziGroup(i.getKey(), i.getValue(), d));
        return groups;
    }

    public List<RPinyin> getPinyin()
    {
        return new ArrayList<>(SetTool.mapping(data, i -> PinyinFormatter.handle(i.getMainPy(), dialect)));
    }

    public boolean isSpecial()
    {
        return ListTool.exist(data, i -> i.getSpecial() != 0);
    }
}
