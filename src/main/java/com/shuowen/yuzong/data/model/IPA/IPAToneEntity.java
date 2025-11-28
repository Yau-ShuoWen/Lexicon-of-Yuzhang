package com.shuowen.yuzong.data.model.IPA;

import lombok.Data;

@Data
public class IPAToneEntity
{
    private Integer standard;
    private String info;

    //TODO:在后期要设计并且把和字典里对音调的描述加入进来
}
