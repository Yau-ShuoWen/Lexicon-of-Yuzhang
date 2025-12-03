package com.shuowen.yuzong.data.mapper.Character;

import com.shuowen.yuzong.data.model.Character.CharEntity;
import com.shuowen.yuzong.data.model.Character.CharPinyin;
import com.shuowen.yuzong.data.model.Character.CharSimilar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CharMapper
{
    /**
     * 使用简繁体寻找汉字
     */
    List<CharEntity> findHanziByScTc(String hanzi, String dialect);

    /**
     * 使用简繁体、模糊识别寻找汉字
     */
    List<CharEntity> findHanziByVague(String hanzi, String dialect);

    /**
     * 使用确定的简体或者繁体寻找汉字
     */
    List<CharEntity> findHanziByScOrTc(String hanzi, String lang, String dialect);

    /**
     * 通过主键寻找汉字
     */
    CharEntity findHanziByCharId(Integer id, String dialect);

    /**
     * 通过汉字外键寻找相似字
     */
    List<CharSimilar> findHanziSimilarByCharId(Integer id, String dialect);

    /**
     * 通过汉字外键寻找读音
     */
    List<CharPinyin> findHanziPinyinByCharId(Integer id, String dialect);

    /**
     * 插入主表数据
     */
    void insertChar(@Param ("ch") CharEntity ch, @Param ("dialect") String dialect);

    /**
     * 插入Similar表
     */
    void insertCharSimilar(@Param ("ch") CharSimilar ch, @Param ("dialect") String dialect);

    /**
     * 插入Pinyin表
     */
    void insertCharPinyin(@Param ("ch") CharPinyin ch, @Param ("dialect") String dialect);

    /**
     * 更新主表数据
     */
    void updateCharById(@Param ("ch") CharEntity ch, @Param ("dialect") String dialect);

    /**
     * 更新Similar表数据
     */
    void updateCharSimilarById(@Param ("ch") CharSimilar ch, @Param ("dialect") String dialect);

    /**
     * 更新Pinyin表数据
     */
    void updateCharPinyinById(@Param ("ch") CharPinyin ch, @Param ("dialect") String dialect);

    /**
     * 删除Similar表数据
     */
    void deleteCharSimilarById(Integer id, String dialect);

    /**
     * 删除Pinyin表数据
     */
    void deleteCharPinyinById(Integer id, String dialect);

    /**
     * 根据主键寻找上一条数据的主键号码
     * */
    Integer findPreviousItem(Integer id, String dialect);

    /**
     * 根据主键寻找下一条数据的主键号码
     * */
    Integer findNextItem(Integer id, String dialect);
}