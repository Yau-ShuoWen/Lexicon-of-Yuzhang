package com.shuowen.yuzong.multimedia.audio;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PinyinUploadRequest
{
    private MultipartFile file;
    private String dialect;
    private String pinyinKey;
}
