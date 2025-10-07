package com.shuowen.yuzong.dao.model.IPA;

import lombok.Data;

/**
 * 国际音标音节和音节结构的Model类
 * */
@Data
public class IPASyllableEntity
{
    private String standard;
    private String info;
    private String code;

    //TODO:在后期要设计并且把和字典里对音调的描述加入进来
}
