package com.shuowen.yuzong.data.model.Character;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HanziEntity
{
    private Integer id;
    private String sc;
    private String tc;
    private String mainPy;
    private String pyCode;
    private Integer special;

    private String similar;
    private String variantPy;
    private String mdrInfo;
    private String note;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer status;
}
