package com.shuowen.yuzong.media.audio;

import lombok.Data;

import java.util.Date;

@Data
public class Audio
{
    private Long id;
    private String name;
    private String originalName;
    private String objectName;
    private String url;
    private String format;
    private Long size;
    private Integer duration;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}