package com.shuowen.yuzong.media.image;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ImageMapper
{
    @Insert("""
            INSERT INTO image (original_name, object_name, format, size, status, has_thumbnail, create_time, update_time)
            VALUES (#{originalName}, #{objectName}, #{format}, #{size}, 1, #{hasThumbnail}, NOW(), NOW())
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Image image);

    @Select("SELECT * FROM image WHERE id = #{id}")
    Image findById(Long id);

    @Select("SELECT * FROM image WHERE object_name LIKE CONCAT('image/diary/', #{diaryId}, '/', #{publicId}, '.%') LIMIT 1")
    Image findDiaryByPublicId(@Param("diaryId") Integer diaryId, @Param("publicId") String publicId);

    @Select("SELECT * FROM image WHERE object_name LIKE CONCAT('image/diary/', #{diaryId}, '/%') AND status = 1")
    List<Image> listDiaryImages(Integer diaryId);

    @Select("SELECT * FROM image WHERE object_name LIKE 'image/diary/%' AND recycle_time IS NOT NULL AND status = 1 ORDER BY recycle_time DESC LIMIT 100")
    List<Image> listDiaryRecycle();

    @Update("UPDATE image SET recycle_time = COALESCE(recycle_time, NOW()) WHERE id = #{id} AND status = 1")
    int markUnreferenced(Long id);

    @Update("UPDATE image SET recycle_time = NULL WHERE id = #{id} AND status = 1")
    int markReferenced(Long id);

    @Select("SELECT * FROM image WHERE status IN (1, 2) ORDER BY id DESC LIMIT 100")
    List<Image> listActive();

    @Update("UPDATE image SET status = 2, update_time = NOW() WHERE id = #{id} AND status = 1")
    int markDeleting(Long id);

    @Update("UPDATE image SET status = 0, update_time = NOW() WHERE id = #{id} AND status = 2")
    int finishDelete(Long id);
}
