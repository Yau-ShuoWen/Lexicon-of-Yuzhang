package com.shuowen.yuzong.ysw.data.mapper;

import com.shuowen.yuzong.ysw.data.model.CipherEntity;
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
