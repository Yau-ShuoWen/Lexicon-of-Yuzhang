package com.shuowen.yuzong.data.model.Word;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CiyuEntity
{
    private Integer id;
    private String sc;
    private String tc;
    private Integer special;

    private String mainPy;
    private String variantPy;
    private String similar;   // 只读
    private String mean;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
