package com.shuowen.yuzong.multimedia.audio;

import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AudioUploadRequest
{
    private MultipartFile file;
    private String dialect;
    private String type;
    private ScTcText refKey;
}