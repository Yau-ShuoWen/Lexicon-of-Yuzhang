package com.shuowen.yuzong.study.wordcard;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WordCardMapper
{
    List<WordCardEntity> findRandom(String dialect, int limit);

    List<WordCardEntity> findByIds(@Param("ids") List<Integer> ids, @Param("dialect") String dialect);

    List<WordCardEntity> findAll(String dialect);

    WordCardEntity findById(Integer id, String dialect);

    void insert(WordCardEntity entity);

    void update(WordCardEntity entity);

    void delete(Integer id, String dialect);
}
