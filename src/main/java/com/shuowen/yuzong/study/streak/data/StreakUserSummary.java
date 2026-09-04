package com.shuowen.yuzong.study.streak.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreakUserSummary
{
    private Integer id;
    private String username;
    private String phone;
}
