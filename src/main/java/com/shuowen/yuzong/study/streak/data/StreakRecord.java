package com.shuowen.yuzong.study.streak.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class StreakRecord
{
    private LocalDate date;
    private String status;
}
