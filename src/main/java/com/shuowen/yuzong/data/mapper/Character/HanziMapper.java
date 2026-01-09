package com.shuowen.yuzong.data.mapper.Character;

import com.shuowen.yuzong.data.model.Character.HanziEntity;
import com.shuowen.yuzong.data.model.Character.HanziPinyin;
import com.shuowen.yuzong.data.model.Character.HanziSimilar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HanziMapper
{
    /**
     * 使用简繁体寻找汉字
     */
    List<HanziEntity> findHanziByScTc(String hanzi, String dialect);

    /**
     * 使用简繁体、模糊识别寻找汉字
     */
    List<HanziEntity> findHanziByVague(String hanzi, String dialect);

    /**
     * 使用确定的简体或者繁体寻找汉字
     */
    List<HanziEntity> findHanziByScOrTc(String hanzi, String lang, String dialect);

    /**
     * 获得所有数据
     */
    List<HanziEntity> getAllHanzi(String dialect);


    /**
     * 通过主键寻找汉字
     */
    HanziEntity findHanziByCharId(Integer id, String dialect);

    /**
     * 通过唯一键寻找汉字
     */
    HanziEntity findByUniqueKey(@Param ("ch") HanziEntity ch, @Param ("dialect") String dialect);

    /**
     * 通过汉字外键寻找相似字
     */
    List<HanziSimilar> findHanziSimilarByCharId(Integer id, String dialect);

    /**
     * 通过汉字外键寻找读音
     */
    List<HanziPinyin> findHanziPinyinByCharId(Integer id, String dialect);


    /**
     * 插入主表数据
     */
    void insertChar(@Param ("ch") HanziEntity ch, @Param ("dialect") String dialect);

    /**
     * 插入相似汉字表
     */
    void insertCharSimilar(@Param ("ch") HanziSimilar ch, @Param ("dialect") String dialect);

    /**
     * 插入读音变体表
     */
    void insertCharPinyin(@Param ("ch") HanziPinyin ch, @Param ("dialect") String dialect);


    /**
     * 更新主表数据
     */
    void updateCharById(@Param ("ch") HanziEntity ch, @Param ("dialect") String dialect);

    /**
     * 更新相似汉字表数据
     */
    void updateCharSimilarById(@Param ("ch") HanziSimilar ch, @Param ("dialect") String dialect);

    /**
     * 更新读音变体表数据
     */
    void updateCharPinyinById(@Param ("ch") HanziPinyin ch, @Param ("dialect") String dialect);


    /**
     * 删除相似汉字表数据
     */
    void deleteCharSimilarById(Integer id, String dialect);

    /**
     * 删除读音变体表数据
     */
    void deleteCharPinyinById(Integer id, String dialect);


    /**
     * 随机选择一个内容主键
     */
    Integer getRandomId(String dialect);

    /**
     * 根据主键寻找上一条数据的主键号码
     */
    Integer findPreviousId(Integer id, String dialect);

    /**
     * 根据主键寻找下一条数据的主键号码
     */
    Integer findNextId(Integer id, String dialect);


    /**
     * 展示用，返回字的数据行数
     */
    Integer findRowCountInHanziTable(String dialect);

    /**
     * 展示用，返回拼音数据行数
     */
    Integer findRowCountInPinyinTable(String dialect);
}