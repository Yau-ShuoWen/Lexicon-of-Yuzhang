package com.shuowen.yuzong.study.wordcard.data;

import com.shuowen.yuzong.study.wordcard.WordCardUpdate;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChapterWordCardGroup
{
    private Integer partId;
    private Integer partOrder;
    private ScTcText partTitle;
    private Integer chapterId;
    private Integer chapterOrder;
    private ScTcText chapterTitle;
    private List<WordCardUpdate> wordCards;
}
