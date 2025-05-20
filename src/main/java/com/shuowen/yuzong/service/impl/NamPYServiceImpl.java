package com.shuowen.yuzong.service.impl;

import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.dao.mapper.PinyinIPA.NamIPAMapper;
import com.shuowen.yuzong.dao.model.PinyinIPA.NamIPA;
import com.shuowen.yuzong.service.Interface.NamPinyinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NamPYServiceImpl implements NamPinyinService
{
    @Autowired
    private NamIPAMapper namPY;


    /**
     * 根据拼音找号码
     * */
    public String getCode(NamPinyin py)
    {
        return namPY.findByPinyin(py.getPinyin()).getCode();
    }


    /**
     * 比如新加了拼音，就调用一遍，把code是空的全部更新
     * */
    public void updateCode()
    {

    }

    /**
     * 如果换了新的编码方式，把code全部更新一遍
     * */
    public void reconstructionCode()
    {

    }

    public NamIPA getAllByPinyin(String s)
    {
        return namPY.findByPinyin(s);
    }

    /**
     * 获得列表，会根据code排列
     * */
    public List<NamIPA> getAll()
    {
        return namPY.findAll();
    }

    public String getIPA(NamPinyin pinyin, String dict)
    {
        if (pinyin.isInvalid()) return null;
        NamIPA data = getAllByPinyin(pinyin.getPinyin());
        dict = dict.toLowerCase();

        return switch (dict)
        {
            case "ncdict" -> data.getNcDict();
            case "gansum" -> data.getGanSum();
            case "chidial" -> data.getChiDial();
            case "ncrecord" -> data.getNcRecord();
            case "ncstudy" -> data.getNcStudy();
            case "ncphon" -> data.getNcPhon();
            default -> data.getStandard();
        };
    }
}
