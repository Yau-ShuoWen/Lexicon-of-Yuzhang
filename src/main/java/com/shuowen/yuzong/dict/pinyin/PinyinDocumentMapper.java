package com.shuowen.yuzong.dict.pinyin;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PinyinDocumentMapper
{
    @Select("SELECT payload FROM NC.pinyin_document WHERE dialect = #{dialect}")
    String get(@Param("dialect") String dialect);

    @Insert("INSERT INTO NC.pinyin_document (dialect, payload) VALUES (#{dialect}, #{payload}) " +
            "ON DUPLICATE KEY UPDATE payload = #{payload}")
    void upsert(@Param("dialect") String dialect, @Param("payload") String payload);
}
