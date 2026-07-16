package com.shuowen.yuzong.ysw.data.domain.diary;

import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

@Data
public class DiaryText
{
    private LocalDate date;
    private Maybe<UString> content;
    private Maybe<UString> abridge;

    private LocalDate startDate;
    private LocalDate finalizeDate;

    private Integer id;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public DiaryText(DiaryEntity d, Language l)
    {
        Function<String, Maybe<UString>> fun = s -> Maybe.uncertain(s).handleIfExist(i -> ScTcText.get(i, l));

        date = d.getDate();
        content = fun.apply(d.getContent());
        abridge = fun.apply(d.getContent().split("\\R", 2)[0]);

        startDate = d.getStartDate();
        finalizeDate = d.getFinalizeDate();

        id = d.getId();
        createdTime = d.getCreatedTime();
        updatedTime = d.getUpdatedTime();
    }
}
