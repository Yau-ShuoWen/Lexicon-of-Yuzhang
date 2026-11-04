package com.shuowen.yuzong.study.level;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudyLevelFinishResponse
{
    private String attemptId;
    private boolean completed;
    private boolean streakSettled;
    private int questionCount;
    private int completedCount;
}
