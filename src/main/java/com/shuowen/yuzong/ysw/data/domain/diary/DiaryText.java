package com.shuowen.yuzong.ysw.data.domain.diary;

import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Twin;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DiaryText
{
    private LocalDate date;
    private UString title;
    private UString content;
    private UString abridge;

    private LocalDate startDate;
    private LocalDate finalizeDate;

    private Integer id;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public DiaryText(DiaryEntity d, Language l)
    {
        date = d.getDate();

        var text = initTitle(d.getContent());

        title = text.getLeft().get(l);
        content = text.getRight().get(l);

        startDate = d.getStartDate();
        finalizeDate = d.getFinalizeDate();

        id = d.getId();
        createdTime = d.getCreatedTime();
        updatedTime = d.getUpdatedTime();
    }

    private Twin<ScTcText> initTitle(String text)
    {
        String[] split = text.split("\\R\\R+", 2);

        String title, content;
        if (split.length == 1)
        {
            title = "";
            content = split[0];
        }
        else
        {
            title = split[0].replace(" ", "   ");
            content = split[1];
        }
        return Twin.of(title, content).map(ScTcText::new);
    }
}
