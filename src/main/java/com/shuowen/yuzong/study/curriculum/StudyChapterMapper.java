package com.shuowen.yuzong.study.curriculum;

import com.shuowen.yuzong.study.curriculum.data.StudyChapterEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudyChapterMapper
{
    List<StudyChapterEntity> findByPartId(@Param("partId") Integer partId);

    int countByPartId(@Param("partId") Integer partId);

    void insert(StudyChapterEntity entity);

    void update(StudyChapterEntity entity);

    void updateOrder(@Param("id") Integer id, @Param("partId") Integer partId,
                     @Param("chapterOrder") Integer chapterOrder);

    int delete(@Param("id") Integer id, @Param("partId") Integer partId);
}
