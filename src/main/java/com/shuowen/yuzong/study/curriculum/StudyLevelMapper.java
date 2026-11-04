package com.shuowen.yuzong.study.curriculum;

import com.shuowen.yuzong.study.curriculum.data.StudyLevelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudyLevelMapper
{
    List<StudyLevelEntity> findByPartId(@Param("partId") Integer partId);

    List<StudyLevelEntity> findPublishedByPartId(@Param("partId") Integer partId);

    StudyLevelEntity findById(@Param("id") Integer id, @Param("dialect") String dialect);

    void insert(StudyLevelEntity entity);

    void update(StudyLevelEntity entity);
}
