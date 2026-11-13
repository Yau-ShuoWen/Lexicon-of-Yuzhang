package com.shuowen.yuzong.dict.data.model.Word;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shuowen.yuzong.util.version.ChangeDetectable;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class CiyuSimilar implements ChangeDetectable<CiyuSimilar>
{
    Integer id;
    Integer wordId;
    String sc;
    String tc;
    Integer type;
    String mandarinRelation;
    String description;
    Integer sort;

    // 比较内容

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CiyuSimilar ct = (CiyuSimilar) o;
        if (id != null && id > 0 || ct.id != null && ct.id > 0)
            return id != null && id > 0 && id.equals(ct.id);
        return Objects.equals(sc, ct.sc) && Objects.equals(tc, ct.tc) && Objects.equals(type, ct.type);
    }

    @Override
    public int hashCode()
    {
        return id != null && id > 0 ? id.hashCode() : Objects.hash(sc, tc, type);
    }

    @JsonIgnore
    @Override
    public boolean isNewItem()
    {
        return id == null || id <= 0;
    }

    @Override
    public List<String> getChangedFields(CiyuSimilar other)
    {
        List<String> res = new ArrayList<>();
        if (!Objects.equals(sc, other.sc)) res.add("sc");
        if (!Objects.equals(tc, other.tc)) res.add("tc");
        if (!Objects.equals(type, other.type)) res.add("type");
        if (!Objects.equals(mandarinRelation, other.mandarinRelation)) res.add("mandarinRelation");
        if (!Objects.equals(description, other.description)) res.add("description");
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
