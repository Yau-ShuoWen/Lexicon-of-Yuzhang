package com.shuowen.yuzong.ysw.diary.data;

import java.time.LocalDate;
import java.util.Map;
import com.shuowen.yuzong.media.image.ImageVO;

public record DiaryEditData(
        Integer id,
        LocalDate date,
        Integer sort,
        String content,
        String forFriend,
        String forStranger,
        LocalDate startDate,
        LocalDate finalizeDate,
        String visibility,
        Map<String, ImageVO> images
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
                visibility,
                Map.of()
        );
    }
}
