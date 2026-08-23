package com.shuowen.yuzong.ysw.data.domain.diary;

import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Twin;
import com.shuowen.yuzong.ysw.data.model.diary.DiaryEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private String viewMode;
    private List<String> availableViews;

    public DiaryText(DiaryEntity d, Language l)
    {
        this(d, l, d.getContent(), null, null);
    }

    public DiaryText(DiaryEntity d, Language l, String body)
    {
        this(d, l, body, null, null);
    }

    public DiaryText(DiaryEntity d, Language l, String body, String viewMode, List<String> availableViews)
    {
        date = d.getDate();

        var text = initTitle(body);

        title = text.getLeft().get(l);
        content = text.getRight().get(l);

        startDate = d.getStartDate();
        finalizeDate = d.getFinalizeDate();

        id = d.getId();
        createdTime = d.getCreatedTime();
        updatedTime = d.getUpdatedTime();
        this.viewMode = viewMode;
        this.availableViews = availableViews;
    }

    private Twin<ScTcText> initTitle(String text)
    {
        if (text == null)
        {
            text = "";
        }
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
