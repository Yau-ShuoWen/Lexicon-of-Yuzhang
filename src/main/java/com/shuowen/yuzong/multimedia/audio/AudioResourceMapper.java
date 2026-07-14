package com.shuowen.yuzong.multimedia.audio;

import org.apache.ibatis.annotations.*;

@Mapper
public interface AudioResourceMapper
{

    @Insert ("""
                INSERT INTO NC.audio_resource(dialect, type, sc, tc, file_path)
                VALUES(#{dialect}, #{type}, #{sc}, #{tc}, #{filePath})
            """)
    void insert(AudioResource ar);

    @Select ("""
            SELECT COUNT(*) FROM audio_resource
            WHERE dialect = #{dialect} AND type = 'pinyin' AND sc = #{pinyinKey}
            """)
    int countPinyinAudio(String dialect, String pinyinKey);

    @Select ("""
            SELECT * FROM NC.audio_resource
               WHERE dialect = #{dialect}
                 AND sc = #{sc}
                 AND type = #{type}
               LIMIT 1
            """)
    AudioResource findByDialectAndScAndType(String dialect, String sc, String type);

    @Delete ("DELETE FROM NC.audio_resource WHERE id = #{id}")
    int deleteById(Long id);
}