package com.shuowen.yuzong.dao.model.Character;

import lombok.Data;

/**
 * 普通话和南昌话连接的类
 * */
@Data
public class MdrDialectChar
{
    Integer HId;
    String hpComb;

    Integer leftId;
    Integer rightId;

    Integer DId;
    String hanzi;
    String hantz;
    String stdPy;
    String mulPy;
}
