package com.shuowen.yuzong.study.curriculum.data;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习部分中的一个可游玩的关卡。
 */
@Data
public class StudyLevelEntity
{
    private Integer id;
    private Integer partId;
    private Integer levelOrder;
    private String title;       // ScTcText JSON
    private String description; // ScTcText JSON
    private String questionType;
    private Integer questionCount;
    private Integer pairCount;
    private String cardIds;     // List<Integer> JSON；为空时随机取词卡
    private Integer status;     // 0 草稿，1 发布
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
