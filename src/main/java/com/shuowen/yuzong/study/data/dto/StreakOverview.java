package com.shuowen.yuzong.study.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StreakOverview
{
    private int currentStreak;
    private int longestStreak;
    private int totalStudyDays;
    private int completedDays;
    private int protectedDays;
    private int protectionBalance;
    private String todayStatus;
    private List<StreakRecord> records;
}
