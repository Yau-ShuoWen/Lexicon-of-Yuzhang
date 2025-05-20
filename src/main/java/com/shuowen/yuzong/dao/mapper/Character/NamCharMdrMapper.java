package com.shuowen.yuzong.dao.mapper.Character;

import com.shuowen.yuzong.dao.model.Character.NamCharMdr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NamCharMdrMapper
{
    @Select ("select * from NC.nam_char_mdr where n = #{n}")
    NamCharMdr selectByMdr(@Param ("n") Integer n);

    @Select ("select * from NC.nam_char_mdr where n = #{n}")
    NamCharMdr selectByNam(@Param ("n") Integer n);
}
