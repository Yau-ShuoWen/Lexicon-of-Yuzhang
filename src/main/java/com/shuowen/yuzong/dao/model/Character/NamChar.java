package com.shuowen.yuzong.dao.model.Character;

import lombok.Data;

@Data
public class NamChar
{
    private Integer id;
    private String hanzi;
    private String std_Py;
    private Integer special;

    private String fitting_Hanzi;
    private String mul_Py;
    private String ipa_exp;
    private String mean;
    private String note;
    private String refer;

    private String fit0;
    private String fit1;
    private String fit2;
    private String fit3;
    private String fit4;
}
