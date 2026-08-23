package com.shuowen.yuzong.ysw.data.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AlphabetMapper
{
    @Select ("SELECT introduce FROM NC.ysw_alphabet WHERE name = #{code}")
    String getIntroduce(String code);

    @Select ("SELECT pinyin_table FROM NC.ysw_alphabet WHERE name = #{code}")
    String getPinyinTable(String code);
}
