package com.shuowen.yuzong.media.audio;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AudioMapper
{

    @Insert ("""
            INSERT INTO NC.audio
            (name, original_name, object_name, format, size )
            VALUES
            (#{name}, #{originalName}, #{objectName}, #{format}, #{size})
            """)
    int insert(Audio audio);


    /**
     * 按 OSS key 前缀查询音频列表（虚拟文件夹浏览）。
     * 例如 prefix = "audio/wuh/char/" 只查该文件夹下的音频。
     */
    @Select ("SELECT * FROM audio WHERE status=1 AND object_name LIKE CONCAT(#{prefix}, '%') ORDER BY create_time DESC ")
    List<Audio> listByFolder(String prefix);


    @Select ("SELECT * FROM audio WHERE id=#{id}")
    Audio findById(Long id);

    @Update ("UPDATE NC.audio SET status=0 WHERE id=#{id}")
    int delete(Long id);
}