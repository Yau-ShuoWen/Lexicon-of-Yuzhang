package com.shuowen.yuzong.data.model.Word;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WordPinyinMap
{
    private Integer id;
    private Integer wordId;
    private Integer charIndex;
    private Integer pinyinId;
    private Integer pinyinOrder;
    private LocalDateTime createdAt;
}
