package com.shuowen.yuzong.service.impl.Reference;

import com.shuowen.yuzong.data.domain.Pinyin.PinyinConfig;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import com.shuowen.yuzong.data.domain.Reference.RefItem;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional (rollbackFor = {Exception.class})
public class RefTestService
{
    @Autowired
    private RefMapper ck;

    public void checkPinyin(DictCode dict, final PinyinConfig data)
    {
        for (var i : ck.findAll(dict.getCode()))
        {
            try
            {
                new RefItem(i, data);
            } catch (Exception e)
            {
                System.out.println(i);
                System.out.println(e);
            }
        }
    }
}
