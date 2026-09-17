package com.shuowen.yuzong.dict.hanzi.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HanziEntity
{
    private Integer id;
    private String sc;
    private String tc;
    /** 数据库 JSON；结构化转换统一放在 domain 层。 */
    private String pinyin;
    private Integer special;

    private String similar;
    private String note;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer status;
}
