package com.shuowen.yuzong.ysw.diary.data;

import java.time.LocalDate;

public record DiaryCreateRequest(
        LocalDate date,
        Integer sort
)
{
}
