package com.shuowen.yuzong.service.impl;

import com.shuowen.yuzong.dao.mapper.Character.MdrCharMapper;
import com.shuowen.yuzong.dao.model.Character.MdrChar;
import com.shuowen.yuzong.service.Interface.MandarinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MdrHZServiceImpl implements MandarinService
{
    @Autowired
    private MdrCharMapper mdrHZ;

    public List<MdrChar> getMandarinByHanzi(String hanzi)
    {
        return mdrHZ.getByHanziMultiple(hanzi);
    }

    public List<MdrChar> getAllMandarin()
    {
        return mdrHZ.getAllMandarin();
    }


}