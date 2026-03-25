package com.shuowen.yuzong.data.mapper.Word;

import com.shuowen.yuzong.data.model.Word.CiyuEntity;
import com.shuowen.yuzong.data.model.Word.CiyuSimilar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CiyuMapper
{
    /**
     * 使用简繁体句子寻找词语，打开模糊识别
     */
    List<CiyuEntity> findCiyuByVagueInRange(String ciyu, String dialect);

    /**
     * 通过主键寻找词语
     */
    CiyuEntity findCiyuByWordId(Integer id, String dialect);

    /**
     * 使用简繁体句子寻找词语
     */
    List<CiyuEntity> findCiyuByScTcInRange(String ciyu, String dialect);

    List<CiyuEntity> getAllCiyu(String dialect);

    /**
     * 通过词语外键寻找相似字
     */
    List<CiyuSimilar> findCiyuSimilarByWordId(Integer id, String dialect);

    /**
     * 查找唯一键
     */
    CiyuEntity findByUniqueKey(@Param ("wd") CiyuEntity wd, @Param ("dialect") String dialect);

    // 修改数据 ----------------------------------------------------------------

    /**
     * 插入主表数据
     */
    void insertWord(@Param ("wd") CiyuEntity wd, @Param ("dialect") String dialect);

    /**
     * 插入相似词语表
     */
    void insertWordSimilar(@Param (("wd")) CiyuSimilar wd, @Param ("dialect") String dialect);

    /**
     * 更新主表数据
     */
    void updateWordById(@Param ("wd") CiyuEntity wd, @Param ("dialect") String dialect);

    /**
     * 更新相似词语表数据
     */
    void updateWordSimilarById(@Param ("wd") CiyuSimilar wd, @Param ("dialect") String dialect);

    /**
     * 删除读音变体表数据
     */
    void deleteWordSimilarById(Integer id, String dialect);

    // 跳转 ----------------------------------------------------------------


    // 展示 -----------------------------------------------------------------

    /**
     * 展示用，返回词语数据行数
     */
    Integer findRowCountInCiyuTable(String dialect);
}
