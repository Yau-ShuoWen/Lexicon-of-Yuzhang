package com.shuowen.yuzong.data.mapper.Character;

import com.shuowen.yuzong.data.model.Character.CharEntity;
import com.shuowen.yuzong.data.model.Character.CharPinyin;
import com.shuowen.yuzong.data.model.Character.CharSimilar;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NamCharMapper
{
    /**
     * 使用简繁体寻找汉字
     */
    List<CharEntity> findHanziByScTc(String hanzi);

    /**
     * 使用简繁体、模糊识别寻找汉字
     */
    List<CharEntity> findHanziByVague(String hanzi);

    /**
     * 使用确定的简体或者繁体寻找汉字
     */
    List<CharEntity> findHanziByScOrTc(String hanzi, String lang);

    /**
     * 通过主键寻找汉字
     */
    CharEntity findHanziByCharId(Integer id);

    /**
     * 通过汉字外键寻找相似字
     */
    List<CharSimilar> findHanziSimilarByCharId(Integer id);

    /**
     * 通过汉字外键寻找读音
     */
    List<CharPinyin> findHanziPinyinByCharId(Integer id);

    /**
     * 插入主表数据
     */
    void insertChar(CharEntity ch);

    /**
     * 插入Similar表
     */
    void insertCharSimilar(CharSimilar c);

    /**
     * 插入Pinyin表
     */
    void insertCharPinyin(CharPinyin ch);

    /**
     * 更新主表数据
     */
    void updateCharById(CharEntity ch);

    /**
     * 更新Similar表数据
     */
    void updateCharSimilarById(CharSimilar ch);

    /**
     * 更新Pinyin表数据
     */
    void updateCharPinyinById(CharPinyin ch);

    /**
     * 删除Similar表数据
     */
    void deleteCharSimilarById(Integer id);

    /**
     * 删除Pinyin表数据
     */
    void deleteCharPinyinById(Integer id);

    Integer findPreviousItem(Integer id);

    Integer findNextItem(Integer id);
}