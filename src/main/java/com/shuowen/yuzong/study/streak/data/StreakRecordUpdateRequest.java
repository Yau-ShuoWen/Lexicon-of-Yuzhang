package com.shuowen.yuzong.study.streak.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StreakRecordUpdateRequest
{
    private LocalDate date;
    private String status;
}
