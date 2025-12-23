package com.shuowen.yuzong.data.model.Character;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CharEntity
{
    private Integer id;
    private String hanzi;
    private String hantz;
    private String stdPy;
    private Integer special;

    private String similar; // Map<String,List<String>>
    private String mulPy;   // Map<String,Map<String,String>>
    private String mdrInfo; // List<String>
    private String ipaExp;  // Map<String,String>
    private String mean;    // Map<String,List<String>>
    private String note;    // Map<String,List<Map<String,String>>>
    private String refer;   // Map<String,List<String>>

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
