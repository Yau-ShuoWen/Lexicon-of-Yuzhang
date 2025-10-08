package com.shuowen.yuzong.dao.mapper.Character;

import com.shuowen.yuzong.dao.model.Character.CharEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NamCharMapper
{
    /**
     * 通过主键寻找汉字
     */
    CharEntity selectByPrimaryKey(Integer id);

    /**
     * 使用简繁体寻找汉字
     */
    List<CharEntity> findByHanziScTc(String hanzi);

    /**
     * 使用简繁体、模糊识别寻找汉字
     * 寻找：字表里的简体字和繁体字、模糊识别表里的简体字和繁体字
     */
    List<CharEntity> findByHanziVague(String hanzi);

    /**
     * 插入数据
     */
    void insert(CharEntity charEntity);
}