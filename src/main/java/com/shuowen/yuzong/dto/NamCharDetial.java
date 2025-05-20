package com.shuowen.yuzong.dto;


import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NamCharDetial
{
    private String hanzi;
    private String stdPy;
    private List<String> fittingHanzi;
    private Map<String, String> mulPy;
    private Map<String, String> ipaExp;
    private List<String> mean;
    private List<String> note;
    private Map<String, String> refer;
}
