package com.shuowen.yuzong.study.curriculum;

import com.shuowen.yuzong.study.curriculum.data.StudyPartEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudyPartMapper
{
    List<StudyPartEntity> findAll(@Param("dialect") String dialect);

    StudyPartEntity findById(@Param("id") Integer id, @Param("dialect") String dialect);

    void insert(StudyPartEntity entity);

    void update(StudyPartEntity entity);

    void updateOrder(@Param("id") Integer id, @Param("dialect") String dialect,
                     @Param("partOrder") Integer partOrder);

    int delete(@Param("id") Integer id, @Param("dialect") String dialect);
}
