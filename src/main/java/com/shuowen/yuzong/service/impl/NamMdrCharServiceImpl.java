package com.shuowen.yuzong.service.impl;

import com.shuowen.yuzong.dao.mapper.Character.MdrCharMapper;
import com.shuowen.yuzong.dao.mapper.Character.NamCharMapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NamMdrCharServiceImpl
{
    @Autowired
    private MdrCharMapper mdrCharMapper;
    @Autowired
    private NamCharMapper namCharMapper;

}
