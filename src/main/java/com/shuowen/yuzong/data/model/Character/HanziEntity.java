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
    private Integer special;

    private String similar;    // Map<String,List<String>>
    private String variantPy;  // Map<String,Map<String,String>>
    private String mdrInfo;    // List<String>
    private String ipa;        // Map<String,String>
    private String mean;       // Map<String,List<String>>
    private String note;       // Map<String,List<Map<String,String>>>
    private String refer;      // Map<String,List<String>>

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
