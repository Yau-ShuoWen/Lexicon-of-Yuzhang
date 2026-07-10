package com.shuowen.yuzong.ysw.data.domain.diary;

import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

@Data
public class DiaryDigest
{
    private LocalDate date;
    private Maybe<UString> abridge;
    private LocalDate startDate;
    private LocalDate finalizeDate;
    private Integer id;
    private LocalDateTime updatedTime;

    public DiaryDigest(DiaryEntity d, Language l)
    {
        date = d.getDate();
        abridge = Maybe.create(() -> d.getContent().split("\\R", 2)[0]).handleIfExist(i -> ScTcText.get(i, l));
        startDate = d.getStartDate();
        finalizeDate = d.getFinalizeDate();
        id = d.getId();
        updatedTime = d.getUpdatedTime();
    }
}
