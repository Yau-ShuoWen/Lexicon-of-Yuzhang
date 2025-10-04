package com.shuowen.yuzong.dao.model.PinyinIPA;

import lombok.Data;

@Data
public class IPAToneEntry
{
    private Integer standard;
    private String info;

    //TODO:在后期要设计并且把和字典里对音调的描述加入进来
}
