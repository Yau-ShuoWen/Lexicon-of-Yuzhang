package com.shuowen.yuzong.data.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface KVSMapper
{
    @Select ("SELECT v FROM NC.kvs WHERE k = #{k}")
    String get(String k);

    @Insert ("INSERT INTO NC.kvs (k, v) values (#{k}, #{v})")
    void set(String k, String v);

    @Delete ("DELETE FROM NC.kvs where k = v")
    void del(String k);
}
