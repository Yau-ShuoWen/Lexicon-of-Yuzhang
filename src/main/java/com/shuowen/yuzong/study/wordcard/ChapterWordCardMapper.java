package com.shuowen.yuzong.study.wordcard;

import com.shuowen.yuzong.study.wordcard.data.ChapterWordCardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChapterWordCardMapper
{
    List<ChapterWordCardEntity> findAllByDialect(@Param("dialect") String dialect);

    void deleteAllByDialect(@Param("dialect") String dialect);

    void deleteByWordCardId(@Param("wordCardId") Integer wordCardId);

    void insert(ChapterWordCardEntity entity);
}
