package com.shuowen.yuzong.ysw.data.mapper.diary;

import com.shuowen.yuzong.ysw.data.model.diary.DiaryCatalogEntity;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DiaryMapper
{
    @Select ("SELECT * FROM NC.ysw_diary WHERE id = #{id}")
    DiaryEntity getDiaryById(int id);

    @Update("""
            UPDATE NC.ysw_diary
            SET date = #{date},
                sort = #{sort},
                content = #{content},
                for_friend = #{forFriend},
                for_stranger = #{forStranger},
                start_date = #{startDate},
                finalize_date = #{finalizeDate}
            WHERE id = #{id}
            """)
    int updateForEdit(DiaryEntity diary);

    @Select ("""
            <script>
            SELECT * FROM NC.ysw_diary
            WHERE id = #{id}
              AND
              <choose>
                  <when test='view == "SELF"'>content IS NOT NULL</when>
                  <when test='view == "FRIEND"'>(for_friend IS NOT NULL OR for_stranger IS NOT NULL)</when>
                  <otherwise>for_stranger IS NOT NULL</otherwise>
              </choose>
            </script>
            """)
    DiaryEntity getDiaryByIdVisible(@Param("id") Integer id, @Param("view") String view);

    @Select ("SELECT * FROM NC.ysw_diary ORDER BY date DESC, sort DESC")
    List<DiaryEntity> listAll();

    @Select ("""
            <script>
            SELECT
                YEAR(date) AS year,
                MONTH(date) AS month,
                COUNT(*) AS total,
                MIN(date) AS start_date,
                MAX(date) AS end_date
            FROM NC.ysw_diary
            WHERE
            <choose>
                <when test='view == "SELF"'>content IS NOT NULL</when>
                <when test='view == "FRIEND"'>(for_friend IS NOT NULL OR for_stranger IS NOT NULL)</when>
                <otherwise>for_stranger IS NOT NULL</otherwise>
            </choose>
            GROUP BY YEAR(date), MONTH(date)
            ORDER BY YEAR(date) DESC, MONTH(date) DESC
            </script>
            """)
    List<DiaryCatalogEntity> getCatalog(@Param("view") String view);

    @Select ("""
            <script>
            SELECT * FROM NC.ysw_diary
            WHERE YEAR(date) = #{year}
              AND MONTH(date) = #{month}
              AND
              <choose>
                  <when test='view == "SELF"'>content IS NOT NULL</when>
                  <when test='view == "FRIEND"'>(for_friend IS NOT NULL OR for_stranger IS NOT NULL)</when>
                  <otherwise>for_stranger IS NOT NULL</otherwise>
              </choose>
            ORDER BY date ASC, sort ASC
            </script>
            """)
    List<DiaryEntity> query(@Param ("year") Integer year, @Param ("month") Integer month, @Param("view") String view);

    @Select ("""
            <script>
            SELECT * FROM NC.ysw_diary
            WHERE
            <choose>
                <when test='view == "SELF"'>content IS NOT NULL</when>
                <when test='view == "FRIEND"'>(for_friend IS NOT NULL OR for_stranger IS NOT NULL)</when>
                <otherwise>for_stranger IS NOT NULL</otherwise>
            </choose>
            ORDER BY date DESC, sort DESC
            LIMIT 20
            </script>
            """)
    List<DiaryEntity> getRecent(@Param("view") String view);

    @Select("""
            <script>
            SELECT * FROM NC.ysw_diary
            WHERE (date, sort) &lt; (
                SELECT date, sort FROM NC.ysw_diary WHERE id = #{id}
            )
              AND
              <choose>
                  <when test='view == "SELF"'>content IS NOT NULL</when>
                  <when test='view == "FRIEND"'>(for_friend IS NOT NULL OR for_stranger IS NOT NULL)</when>
                  <otherwise>for_stranger IS NOT NULL</otherwise>
              </choose>
            ORDER BY date DESC, sort DESC
            LIMIT 1
            </script>
            """)
    DiaryEntity selectPrev(@Param("id") Integer id, @Param("view") String view);


    @Select("""
            <script>
            SELECT * FROM NC.ysw_diary
            WHERE (date, sort) &gt; (
                SELECT date, sort FROM NC.ysw_diary WHERE id = #{id}
            )
              AND
              <choose>
                  <when test='view == "SELF"'>content IS NOT NULL</when>
                  <when test='view == "FRIEND"'>(for_friend IS NOT NULL OR for_stranger IS NOT NULL)</when>
                  <otherwise>for_stranger IS NOT NULL</otherwise>
              </choose>
            ORDER BY date ASC, sort ASC
            LIMIT 1
            </script>
            """)
    DiaryEntity selectNext(@Param("id") Integer id, @Param("view") String view);

//    @Select("""
//            SELECT * FROM ysw_diary WHERE content like concat('%',#{query},'%')
//            """)
//    List<DiaryEntity>
}
