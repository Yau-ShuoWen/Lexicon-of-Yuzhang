package com.shuowen.yuzong.ysw.data.dto.diary;

import java.time.LocalDate;

public record DiaryCreateRequest(
        LocalDate date,
        Integer sort
)
{
}
