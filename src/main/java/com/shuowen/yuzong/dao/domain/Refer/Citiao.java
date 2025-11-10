package com.shuowen.yuzong.dao.domain.Refer;

import com.shuowen.yuzong.Tool.DataVersionCtrl.ChangeDetectable;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.dao.dto.Refer.CitiaoEdit;
import com.shuowen.yuzong.dao.model.Refer.ReferEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
public class Citiao implements ChangeDetectable<Citiao>
{
    private int id;
    private String dictionary;
    private int page;
    private UString content;
    private String sort;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Citiao(ReferEntity re)
    {
        id = re.getId();
        dictionary = re.getDictionary();
        page = re.getPage();
        content = UString.of(re.getContent());
        sort = re.getSort();
        createdAt = re.getCreatedAt();
        updatedAt = re.getUpdatedAt();
    }

    public static Citiao of(ReferEntity re)
    {
        return new Citiao(re);
    }

    public static List<Citiao> listOf(List<ReferEntity> list)
    {
        List<Citiao> res = new ArrayList<>();
        for (var i : list) res.add(Citiao.of(i));
        return res;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Citiao ct = (Citiao) o;
        return Objects.equals(dictionary, ct.dictionary) &&
                Objects.equals(page, ct.page) &&
                Objects.equals(content, ct.content);
    }

    public Citiao(CitiaoEdit ce)
    {
        id = ce.getId();
        dictionary = ce.getDictionary();
        page = ce.getPage();
        content = UString.of(ce.getContent());
        sort = ce.getSort();
    }

    public static Citiao of(CitiaoEdit ce)
    {
        return new Citiao(ce);
    }

    /**
     * 某一个泛型擦除的大手又发了力……
     */
    public static List<Citiao> listBy(List<CitiaoEdit> list)
    {
        List<Citiao> res = new ArrayList<>();
        for (var i : list) res.add(Citiao.of(i));
        return res;
    }

    @Override
    public boolean isNewItem()
    {
        return id <= 0;
    }

    @Override
    public List<String> getChangedFields(Citiao other)
    {
        List<String> res = new ArrayList<>();
        if (!Objects.equals(dictionary, other.getDictionary())) res.add("dictionary");
        if (!Objects.equals(page, other.getPage())) res.add("page");
        if (!Objects.equals(content, other.getContent())) res.add("content");
        if (!Objects.equals(sort, other.getSort())) res.add("sort");
        return res;
    }

    @Override
    public Object getUniqueKey()
    {
        return id;
    }

    public ReferEntity transfer()
    {
        ReferEntity re = new ReferEntity();
        re.setId(id);
        re.setDictionary(dictionary);
        re.setPage(page);
        re.setContent(content.toString());
        re.setSort(sort);
        return re;
    }
}
