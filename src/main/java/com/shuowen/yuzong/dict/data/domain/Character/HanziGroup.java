package com.shuowen.yuzong.dict.data.domain.Character;

import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.dict.data.model.Character.HanziEntity;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.ext.set.SetTool;
import com.shuowen.yuzong.util.text.UChar;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String getPinyin()
    {
        return String.join("/",
                SetTool.mapping(data, i -> dialect.trustedCreatePinyin(i.getMainPy()).toRPinyin().toString())
        );
    }

    public boolean isSpecial()
    {
        return ListTool.exist(data, i -> i.getSpecial() != 0);
    }
}
