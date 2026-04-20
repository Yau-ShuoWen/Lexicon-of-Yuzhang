package com.shuowen.yuzong.data.model.Course;

import lombok.Data;

@Data
public class CourseEntity implements Comparable<CourseEntity>
{
    private Integer id;
    private String title;
    private Integer chapter;
    private Integer section;
    private String text;

    @Override
    public int compareTo(CourseEntity o)
    {
        // 先比較 chapter
        int chapterCompare = this.chapter.compareTo(o.chapter);
        if (chapterCompare != 0)
        {
            return chapterCompare;
        }
        // chapter 相同時，再比較 section
        return this.section.compareTo(o.section);
    }
}
