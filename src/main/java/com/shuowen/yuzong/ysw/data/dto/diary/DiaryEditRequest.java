package com.shuowen.yuzong.ysw.data.dto.diary;

import java.time.LocalDate;

public record DiaryEditRequest(
        LocalDate date,
        Integer sort,
        String content,
        String forFriend,
        String forStranger,
        LocalDate startDate,
        LocalDate finalizeDate,
        String visibility
)
{
}
