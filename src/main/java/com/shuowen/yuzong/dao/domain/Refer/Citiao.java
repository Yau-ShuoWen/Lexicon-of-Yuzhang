package com.shuowen.yuzong.dao.domain.Refer;

import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.dao.model.Refer.ReferEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Citiao
{
    private int id;
    private UString dictionary;
    private int page;
    private UString content;
    private String sort;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Citiao(ReferEntity re)
    {
        id=re.getId();
        dictionary=UString.of(re.getDictionary());
        page=re.getPage();
        content=UString.of(re.getContent());
        sort=re.getSort();
        createdAt=re.getCreatedAt();
        updatedAt=re.getUpdatedAt();
    }

    public static Citiao of(ReferEntity re)
    {
        return new Citiao(re);
    }
}
