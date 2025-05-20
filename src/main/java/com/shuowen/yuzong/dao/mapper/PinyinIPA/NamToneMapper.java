package com.shuowen.yuzong.dao.mapper.PinyinIPA;

import com.shuowen.yuzong.dao.model.PinyinIPA.NamTone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NamToneMapper
{
    @Select ("select * from NC.nam_tone where standardC = #{tone}")
    NamTone findByStandardC(@Param ("tone") int tone);
}
