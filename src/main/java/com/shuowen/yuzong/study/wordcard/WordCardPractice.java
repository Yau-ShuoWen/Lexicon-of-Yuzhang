package com.shuowen.yuzong.study.wordcard;

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
