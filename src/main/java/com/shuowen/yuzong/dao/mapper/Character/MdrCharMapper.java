package com.shuowen.yuzong.dao.mapper.Character;

import com.shuowen.yuzong.dao.model.Character.MdrChar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MdrCharMapper
{
    /**
     * 通过汉字查信息（单独）
     */
    @Select ("select * from NC.mdr_char where hanzi = #{hanzi} limit 1")
    MdrChar getByHanziUnique(@Param ("hanzi") String hanzi);

    /**
     * 通过汉字查找（多个）
     */
    @Select ("select * from NC.mdr_char where hanzi = #{hanzi}")
    List<MdrChar> getByHanziMultiple(@Param ("hanzi") String hanzi);

    /**
     * 通过拼音查找同音字
     */
    @Select ("select * from NC.mdr_char where pinyin = #{pinyin} and tone = #{tone}")
    List<MdrChar> getByPinyin(@Param ("pinyin") String pinyin, @Param ("tone") String tone);

    /**列表*/
    @Select ("select * from NC.mdr_char order by zhuyin, pinyin")
    List<MdrChar> getAllMandarin();
}
