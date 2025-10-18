package com.shuowen.yuzong.dao.domain.Refer;

import com.shuowen.yuzong.dao.model.Refer.ReferEntity;

import java.util.Collections;
import java.util.List;

public class CitiaoGroup
{
    List<Citiao> list;

    public CitiaoGroup(List<ReferEntity> re)
    {
        for (var i : re)
        {
            list.add(Citiao.of(i));
        }
    }

    public CitiaoGroup(String str)
    {

    }
}
