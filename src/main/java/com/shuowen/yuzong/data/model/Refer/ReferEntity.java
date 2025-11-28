package com.shuowen.yuzong.data.model.Refer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReferEntity
{
    private int id;
    private String dictionary;
    private int page;
    private String content;
    private String sort;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
