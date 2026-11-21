package com.shuowen.yuzong.media.image;

import lombok.Data;

import java.util.Date;

@Data
public class ImageVO
{
    private Long id;
    /** 正文使用的稳定、非连续图片标识；旧正文的数字 ID 仍可读取。 */
    private String publicId;
    private String originalName;
    private String objectName;
    private String format;
    private Long size;
    private Integer status;
    private Date createTime;
    private Date recycleTime;
    private String url;
    private String thumbnailUrl;
}
