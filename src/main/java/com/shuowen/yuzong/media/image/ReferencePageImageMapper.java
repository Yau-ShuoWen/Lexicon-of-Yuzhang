package com.shuowen.yuzong.media.image;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReferencePageImageMapper
{
    @Select("""
            SELECT image_id FROM reference_page_image
            WHERE dictionary = #{dictionary} AND page_marker_id = #{pageMarkerId}
            """)
    Long findImageId(@Param("dictionary") String dictionary, @Param("pageMarkerId") Integer pageMarkerId);

    @Insert("""
            INSERT INTO reference_page_image (dictionary, page_marker_id, image_id)
            VALUES (#{dictionary}, #{pageMarkerId}, #{imageId})
            ON DUPLICATE KEY UPDATE image_id = VALUES(image_id)
            """)
    int bind(@Param("dictionary") String dictionary, @Param("pageMarkerId") Integer pageMarkerId,
             @Param("imageId") Long imageId);
}
