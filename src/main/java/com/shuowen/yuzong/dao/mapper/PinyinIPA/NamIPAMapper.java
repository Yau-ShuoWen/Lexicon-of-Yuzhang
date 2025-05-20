package com.shuowen.yuzong.dao.mapper.PinyinIPA;

import com.shuowen.yuzong.dao.model.PinyinIPA.NamIPA;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NamIPAMapper
{
    @Select ("select * from NC.nam_ipa where standard = #{pinyin}")
    NamIPA findByPinyin(@Param ("pinyin") String pinyin);

    @Select ("select * from NC.nam_ipa where code = #{code}")
    NamIPA findByCode(@Param ("code") String code);

    @Select ("select * from NC.nam_ipa order by code")
    List<NamIPA> findAll();
}
