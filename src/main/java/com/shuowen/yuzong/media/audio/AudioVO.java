package com.shuowen.yuzong.media.audio;

import lombok.Data;

@Data
public class AudioVO
{
    private Long id;
    private String name;
    private String format;
    private Long size;
    /** 虚拟文件夹路径，例如 wuh/char/114514 */
    private String folderPath;
    private String url;
}