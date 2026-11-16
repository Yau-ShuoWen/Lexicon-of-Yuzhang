package com.shuowen.yuzong.dict.hanzi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shuowen.yuzong.util.version.ChangeDetectable;
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
    Integer hanziId; // 永远不可以在前端修改
    String sc;
    String tc;
    Integer sort;

    // 比较内容 -------------------------------------

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HanziSimilar ct = (HanziSimilar) o;
        if (id != null && id > 0 || ct.id != null && ct.id > 0)
            return id != null && id > 0 && id.equals(ct.id);
        return Objects.equals(sc, ct.sc) && Objects.equals(tc, ct.tc);
    }

    @Override
    public int hashCode()
    {
        return id != null && id > 0 ? id.hashCode() : Objects.hash(sc, tc);
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
