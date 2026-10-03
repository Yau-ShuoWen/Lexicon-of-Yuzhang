package com.shuowen.yuzong.study.level;

import com.shuowen.yuzong.study.wordcard.WordCardPractice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StudyLevelQuestion
{
    private int number;
    private String type;
    private List<WordCardPractice> cards;
}
