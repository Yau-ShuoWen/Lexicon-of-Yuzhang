package com.shuowen.yuzong.dict.hanzi.mapper;

import com.shuowen.yuzong.dict.hanzi.model.HanziEntity;
import com.shuowen.yuzong.dict.hanzi.model.HanziSimilar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface HanziMapper
{
    List<HanziEntity> findHanziByScTc(String hanzi, String dialect);
    List<HanziEntity> findHanziByVague(String hanzi, String dialect);
    List<HanziEntity> findHanziByScOrTc(String hanzi, String lang, String dialect);
    Set<HanziEntity> findHanziByScTcBatch(Set<String> hanzi, String dialect);
    List<HanziEntity> getAllHanzi(String dialect);
    HanziEntity findHanziByCharId(Integer id, String dialect);
    HanziEntity findByUniqueKey(@Param ("ch") HanziEntity ch, @Param ("dialect") String dialect);
    List<HanziSimilar> findHanziSimilarByCharId(Integer id, String dialect);
    void insertChar(@Param ("ch") HanziEntity ch, @Param ("dialect") String dialect);
    void insertCharSimilar(@Param ("ch") HanziSimilar ch, @Param ("dialect") String dialect);
    void updateCharById(@Param ("ch") HanziEntity ch, @Param ("dialect") String dialect);
    void updateCharSimilarById(@Param ("ch") HanziSimilar ch, @Param ("dialect") String dialect);
    void deleteCharSimilarById(Integer id, String dialect);
    Integer getRandomId(String dialect);
    Integer findPrevId(Integer id, String dialect);
    Integer findNextId(Integer id, String dialect);
    Integer findRowCountInHanziTable(String dialect);
    Integer findRowCountInPinyinTable(String dialect);
}
