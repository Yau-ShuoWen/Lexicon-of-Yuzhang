package com.shuowen.yuzong.ysw.data.domain.diary;

import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DiaryDigest
{
    private LocalDate date;
    private UString intro;
    private LocalDate startDate;
    private LocalDate finalizeDate;
    private Integer id;
    private LocalDateTime updatedTime;

    public DiaryDigest(DiaryEntity d, Language l)
    {
        this(d, l, d.getContent());
    }

    public DiaryDigest(DiaryEntity d, Language l, String body)
    {
        date = d.getDate();
        String text = body == null ? "" : body;
        intro = ScTcText.get(text.split("\\R\\R+", 2)[0].replace(" ", "   "), l);
        startDate = d.getStartDate();
        finalizeDate = d.getFinalizeDate();
        id = d.getId();
        updatedTime = d.getUpdatedTime();
    }
}
