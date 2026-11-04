package com.shuowen.yuzong.study.curriculum.data;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习路线中的一个部分。
 *
 * <p>部分只划分学习阶段。是否为空由下级章的数据计算，不在本表存储。</p>
 */
@Data
public class StudyPartEntity
{
    private Integer id;
    private String dialect;
    private Integer partOrder;
    private String title;       // ScTcText JSON
    private String description; // ScTcText JSON
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
