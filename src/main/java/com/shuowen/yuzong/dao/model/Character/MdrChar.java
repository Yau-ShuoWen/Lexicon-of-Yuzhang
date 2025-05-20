package com.shuowen.yuzong.dao.model.Character;

import lombok.Data;

@Data
public class MdrChar
{
    private int id;
    private String hp_comb;
    private String hanzi;
    private String pinyin;
    private String zhuyin;
    private int tone;
}
