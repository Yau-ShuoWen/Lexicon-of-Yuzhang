package com.shuowen.yuzong.multimedia.audio;

import lombok.Data;

@Data
public class AudioResource {

    private Long id;

    private String dialect;
    private String type;

    private String sc;
    private String tc;

    private String filePath;
}