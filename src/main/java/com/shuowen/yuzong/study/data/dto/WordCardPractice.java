package com.shuowen.yuzong.study.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordCardPractice
{
    private Integer id;
    private String putonghua;
    private String word;
    private String pinyin;
}
