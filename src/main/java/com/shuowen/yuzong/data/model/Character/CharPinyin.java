package com.shuowen.yuzong.data.model.Character;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shuowen.yuzong.Tool.DataVersionCtrl.ChangeDetectable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 方言多个拼音的附表
 *
 * @apiNote 因为结构简单，没有转换的必要，所以兼任{@code DAO} {@code domain} {@code DTO}
 */

@Data
public class CharPinyin implements ChangeDetectable<CharPinyin>
{
    Integer id;     // 新增的内容id设置为0
    Integer charId; // 永远不可以在前端修改
    String sc;
    String tc;
    String pinyin;
    Integer sort;


    // 比较内容 -------------------------------------

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharPinyin ct = (CharPinyin) o;
        return Objects.equals(sc, ct.sc) &&
                Objects.equals(tc, ct.tc) &&
                Objects.equals(pinyin, ct.pinyin) &&
                Objects.equals(charId, ct.charId);
    }

    @JsonIgnore
    @Override
    public boolean isNewItem()
    {
        return id == null || id <= 0;
    }

    @Override
    public List<String> getChangedFields(CharPinyin other)
    {
        List<String> res = new ArrayList<>();
        if (!Objects.equals(sc, other.sc)) res.add("sc");
        if (!Objects.equals(tc, other.tc)) res.add("tc");
        if (!Objects.equals(pinyin, other.pinyin)) res.add("pinyin");
        if (!Objects.equals(sort, other.sort)) res.add("sort");
        return res;
    }

    @JsonIgnore
    @Override
    public Object getUniqueKey()
    {
        return id;
    }
}
