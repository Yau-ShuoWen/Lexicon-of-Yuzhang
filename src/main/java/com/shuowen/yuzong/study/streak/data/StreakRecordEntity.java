package com.shuowen.yuzong.study.streak.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreakRecordEntity
{
    private Integer id;
    private Integer userId;
    private LocalDate studyDate;
    private String status;
}
