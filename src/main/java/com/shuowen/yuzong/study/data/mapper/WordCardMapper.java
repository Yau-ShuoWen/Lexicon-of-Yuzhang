package com.shuowen.yuzong.study.data.mapper;

import com.shuowen.yuzong.study.data.model.WordCardEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WordCardMapper
{
    List<WordCardEntity> findRandom(String dialect, int limit);

    List<WordCardEntity> findAll(String dialect);

    WordCardEntity findById(Integer id, String dialect);

    void insert(WordCardEntity entity);

    void update(WordCardEntity entity);

    void delete(Integer id, String dialect);
}
