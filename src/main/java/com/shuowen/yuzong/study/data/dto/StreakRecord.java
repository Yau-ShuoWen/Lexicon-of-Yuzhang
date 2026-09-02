package com.shuowen.yuzong.study.data.dto;

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
