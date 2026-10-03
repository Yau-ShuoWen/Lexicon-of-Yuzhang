package com.shuowen.yuzong.media.image;

import lombok.Data;

import java.util.Date;

@Data
public class ImageVO
{
    private Long id;
    private String originalName;
    private String objectName;
    private String format;
    private Long size;
    private Integer status;
    private Date createTime;
    private String url;
}
