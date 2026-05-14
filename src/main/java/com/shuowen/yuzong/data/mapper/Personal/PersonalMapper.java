package com.shuowen.yuzong.data.mapper.Personal;

import com.shuowen.yuzong.data.model.Personal.CipherEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PersonalMapper
{
    @Select ("SELECT * FROM ysw.secret WHERE id = #{id}")
    CipherEntity selectById(int id);

    @Select ("SELECT * FROM ysw.secret WHERE indexes LIKE CONCAT('%', #{query}, '%')")
    List<CipherEntity> search(String query);
}
