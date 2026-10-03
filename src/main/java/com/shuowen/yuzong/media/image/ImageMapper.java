package com.shuowen.yuzong.media.image;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ImageMapper
{
    @Insert("""
            INSERT INTO image (original_name, object_name, format, size, status, create_time, update_time)
            VALUES (#{originalName}, #{objectName}, #{format}, #{size}, 1, NOW(), NOW())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Image image);

    @Select("SELECT * FROM image WHERE id = #{id}")
    Image findById(Long id);

    @Select("SELECT * FROM image WHERE status IN (1, 2) ORDER BY id DESC LIMIT 100")
    List<Image> listActive();

    @Update("UPDATE image SET status = 2, update_time = NOW() WHERE id = #{id} AND status = 1")
    int markDeleting(Long id);

    @Update("UPDATE image SET status = 0, update_time = NOW() WHERE id = #{id} AND status = 2")
    int finishDelete(Long id);
}
