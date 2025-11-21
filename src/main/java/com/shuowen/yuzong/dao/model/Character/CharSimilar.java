package com.shuowen.yuzong.dao.model.Character;

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
public class CharSimilar implements ChangeDetectable<CharSimilar>
{
    Integer id;     // 新增的内容id设置为0
    Integer charId; // 永远不可以在前端修改
    String hanzi;
    String hantz;

    // 比较内容 -------------------------------------

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharSimilar ct = (CharSimilar) o;
        return Objects.equals(hanzi, ct.hanzi) &&
                Objects.equals(hantz, ct.hantz);
    }

    @JsonIgnore
    @Override
    public boolean isNewItem()
    {
        return id == null || id <= 0;
    }

    @Override
    public List<String> getChangedFields(CharSimilar other)
    {
        List<String> res = new ArrayList<>();
        if (!Objects.equals(hanzi, other.hanzi)) res.add("hanzi");
        if (!Objects.equals(hantz, other.hantz)) res.add("hantz");
        return res;
    }

    @JsonIgnore
    @Override
    public Object getUniqueKey()
    {
        return id;
    }
}
