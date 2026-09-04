package com.shuowen.yuzong.study.loadingtext.data;

import lombok.Data;

import java.time.LocalDateTime;

// table:study_loading_text
@Data
public class LoadingTextEntity
{
    private Integer id;
    private String tip;//ScTcText的json，非可能为null，空字符串是{"sc": "","tc": ""}
    private String tag;//List<Dialect>（List<String>）的json，空List表示对任意Dialect有效
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
