package com.shuowen.yuzong.ysw.diary.data;

import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Twin;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.shuowen.yuzong.media.image.ImageVO;

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
    private Map<String, ImageVO> images = Map.of();

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
        if (text == null) text = "";

        String[] split = text.split("\\R\\R+", 2);

        String title= split[0].replaceAll(" +", "   ");
        String content=(split.length == 1) ? "" :split[1];

        return Twin.of(title, content).map(ScTcText::new);
    }
}
