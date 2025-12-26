package com.shuowen.yuzong.data.model.Word;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WordEntity
{
    private Integer id;
    private Integer constrId;
    private String ciyu;
    private String tszyu;

    private String pinyin;
    private String mulPy;
    private String similar;

    private String mean;
    private String refer;
    private String example;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}