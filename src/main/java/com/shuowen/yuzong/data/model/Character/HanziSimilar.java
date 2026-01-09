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
public class HanziSimilar implements ChangeDetectable<HanziSimilar>
{
    Integer id;     // 新增的内容id设置为0
    Integer charId; // 永远不可以在前端修改
    String sc;
    String tc;

    // 比较内容 -------------------------------------

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HanziSimilar ct = (HanziSimilar) o;
        return Objects.equals(sc, ct.sc) &&
                Objects.equals(tc, ct.tc);
    }

    @JsonIgnore
    @Override
    public boolean isNewItem()
    {
        return id == null || id <= 0;
    }

    @Override
    public List<String> getChangedFields(HanziSimilar other)
    {
        List<String> res = new ArrayList<>();
        if (!Objects.equals(sc, other.sc)) res.add("sc");
        if (!Objects.equals(tc, other.tc)) res.add("tc");
        return res;
    }

    @JsonIgnore
    @Override
    public Object getUniqueKey()
    {
        return id;
    }
}
