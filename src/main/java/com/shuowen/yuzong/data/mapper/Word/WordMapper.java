package com.shuowen.yuzong.data.mapper.Word;

import com.shuowen.yuzong.data.model.Word.WordEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface WordMapper
{

    /**
     * 使用简繁体寻找词语
     */
    List<WordEntity> findCiyuByScTc(String ciyu, String dialect);

    /**
     * 使用简繁体、模糊识别寻找词语<p>
     * 寻找：字表里的简体字和繁体字、模糊识别表里的简体字和繁体字
     */
    List<WordEntity> findCiyuByVague(String ciyu, String dialect);

    /**
     * 使用简繁体句子寻找词语
     */
    List<WordEntity> findCiyuByScTcInRange(String ciyu, String dialect);

    /**
     * 使用简繁体句子寻找词语，打开模糊识别
     */
    List<WordEntity> findCiyuByVagueInRange(String ciyu, String dialect);

    /**
     * 通过主键寻找词语
     */
    WordEntity findCiyuByWordId(Integer id, String dialect);

    /**
     * 精确查询
     */
    List<WordEntity> findCiyuByScOrTc(String ciyu, String lang, String dialect);
}
