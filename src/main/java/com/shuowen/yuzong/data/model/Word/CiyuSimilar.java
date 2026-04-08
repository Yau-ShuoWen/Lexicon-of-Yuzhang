package com.shuowen.yuzong.data.model.Word;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shuowen.yuzong.Tool.DataVersionCtrl.ChangeDetectable;
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

    // 比较内容

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CiyuSimilar ct = (CiyuSimilar) o;
        return Objects.equals(sc, ct.sc) && Objects.equals(tc, ct.tc);
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
        return res;
    }

    @JsonIgnore
    @Override
    public Object getUniqueKey()
    {
        return id;
    }
}
