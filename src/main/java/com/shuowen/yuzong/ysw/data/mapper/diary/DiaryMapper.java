package com.shuowen.yuzong.ysw.data.mapper.diary;

import com.shuowen.yuzong.ysw.data.model.diary.DiaryCatalogEntity;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DiaryMapper
{
    @Select ("SELECT * FROM NC.ysw_diary WHERE id = #{id}")
    DiaryEntity getDiaryById(int id);

    @Select ("SELECT * FROM NC.ysw_diary WHERE date = #{date}")
    DiaryEntity getDiaryByDate(LocalDate date);

    @Select ("""
            SELECT
                YEAR(date) AS year,
                MONTH(date) AS month,
                COUNT(*) AS total,
                MIN(date) AS start_date,
                MAX(date) AS end_date
            FROM NC.ysw_diary
            GROUP BY YEAR(date), MONTH(date)
            ORDER BY YEAR(date) DESC, MONTH(date) DESC
            """)
    List<DiaryCatalogEntity> getCatalog();

    @Select ("""
            <script>
            SELECT *
            FROM NC.ysw_diary
            <where>
                <if test='year != null'>
                    AND YEAR(date) = #{year}
                </if>
                <if test='month != null'>
                    AND MONTH(date) = #{month}
                </if>
                <if test='startDate != null'>
                    AND date <![CDATA[ >= ]]> #{startDate}
                </if>
                <if test='endDate != null'>
                    AND date <![CDATA[ <= ]]> #{endDate}
                </if>
            </where>
            ORDER BY date DESC, id DESC
            LIMIT #{limit}
            </script>
            """)
    List<DiaryEntity> query(
            @Param ("year") Integer year,
            @Param ("month") Integer month,
            @Param ("startDate") LocalDate startDate,
            @Param ("endDate") LocalDate endDate,
            @Param ("limit") Integer limit
    );

    @Select ("SELECT * FROM NC.ysw_diary ORDER BY date DESC, id DESC LIMIT #{limit}")
    List<DiaryEntity> getRecent(int limit);
}
