package com.shuowen.yuzong.dao.mapper;

import com.shuowen.yuzong.dao.model.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper {

    @Insert("INSERT INTO test.student (name, age, gender) VALUES (#{name}, #{age}, #{gender})")
    int insertStudent(Student student);

    @Select("select * from student where name = #{name} limit 1")
    Student findByName(@Param("name") String name);
}
