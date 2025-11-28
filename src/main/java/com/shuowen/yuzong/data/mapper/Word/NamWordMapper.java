package com.shuowen.yuzong.data.mapper.Word;

import com.shuowen.yuzong.data.model.Word.WordEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

@Mapper
public interface NamWordMapper
{
    /**
     * 通过主键寻找词语
     */
    WordEntity selectByPrimaryKey(Integer id);

    /**
     * 使用简繁体寻找词语
     */
    List<WordEntity> findByCiyuScTc(String ciyu);

    /**
     * 使用简繁体、模糊识别寻找词语
     * 寻找：字表里的简体字和繁体字、模糊识别表里的简体字和繁体字
     */
    List<WordEntity> findByCiyuVague(String ciyu);

    /**
     * 使用简繁体句子寻找词语
     * */
    List<WordEntity> findBySentence(String ciyu);

}

