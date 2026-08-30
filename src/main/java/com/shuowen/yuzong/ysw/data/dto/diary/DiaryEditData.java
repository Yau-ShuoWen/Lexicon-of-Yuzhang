package com.shuowen.yuzong.ysw.data.dto.diary;

import com.shuowen.yuzong.ysw.data.domain.diary.DiaryEditVisibility;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryEntity;

import java.time.LocalDate;

public record DiaryEditData(
        Integer id,
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
    public static DiaryEditData of(DiaryEntity diary)
    {
        String visibility = diary.getForStranger() != null
                ? DiaryEditVisibility.STRANGER.value()
                : diary.getForFriend() != null
                    ? DiaryEditVisibility.FRIEND.value()
                    : DiaryEditVisibility.PRIVATE.value();

        return new DiaryEditData(
                diary.getId(),
                diary.getDate(),
                diary.getSort(),
                diary.getContent(),
                diary.getForFriend(),
                diary.getForStranger(),
                diary.getStartDate(),
                diary.getFinalizeDate(),
                visibility
        );
    }
}
