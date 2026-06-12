package com.shuowen.yuzong.data.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogMapper
{
    @Insert ("INSERT INTO NC.log_char (dialect, previous, after, type) VALUE (#{dialect}, #{previous}, #{after}, #{type}) ")
    void insertChar(String dialect, String previous, String after, String type);

    @Insert ("INSERT INTO NC.log_word (dialect, previous, after, type) VALUE (#{dialect}, #{previous}, #{after}, #{type}) ")
    void insertWord(String dialect, String previous, String after, String type);
}
