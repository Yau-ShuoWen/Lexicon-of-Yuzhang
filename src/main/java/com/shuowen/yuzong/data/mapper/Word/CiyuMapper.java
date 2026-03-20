package com.shuowen.yuzong.data.mapper.Word;

import com.shuowen.yuzong.data.model.Word.CiyuEntity;
import org.apache.ibatis.annotations.Mapper;

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

    Integer findRowCountInCiyuTable(String dialect);
}
