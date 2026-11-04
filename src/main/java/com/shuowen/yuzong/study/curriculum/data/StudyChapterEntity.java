package com.shuowen.yuzong.study.curriculum.data;

import lombok.Data;

import java.time.LocalDateTime;

/** 学习部分中的一章。 */
@Data
public class StudyChapterEntity
{
    private Integer id;
    private Integer partId;
    private Integer chapterOrder;
    private String title;
    private String description;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
