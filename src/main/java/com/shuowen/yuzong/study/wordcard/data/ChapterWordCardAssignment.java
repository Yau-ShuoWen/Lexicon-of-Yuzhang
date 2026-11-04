package com.shuowen.yuzong.study.wordcard.data;

import lombok.Data;

import java.util.List;

@Data
public class ChapterWordCardAssignment
{
    private Integer chapterId;
    private List<Integer> wordCardIds;
}
