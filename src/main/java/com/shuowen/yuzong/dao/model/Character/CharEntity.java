package com.shuowen.yuzong.dao.model.Character;

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

    private String similar;//List<String>
    private String mulPy;//Map<String,Map<String,String>>
    private String pyExplain;//List<String>
    private String ipaExp;//Map<String,String>
    private String mean;//Map<String,List<String>>
    private String note;//Map<String,List<String>>
    private String refer;//Map<String,List<String>>

    private String pinyin;
    private String tone;
    private String fit0;
    private String fit1;
    private String fit2;
    private String fit3;
    private String fit4;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
