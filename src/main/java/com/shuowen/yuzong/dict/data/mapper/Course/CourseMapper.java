package com.shuowen.yuzong.dict.data.mapper.Course;

import com.shuowen.yuzong.dict.data.model.Course.CourseEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseMapper
{
    List<CourseEntity> getCatalog(String dialect);

    List<CourseEntity> getArticalById(String dialect, Integer id);
}
