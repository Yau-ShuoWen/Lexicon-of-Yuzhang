package com.shuowen.yuzong.Linguistics.Format;

import lombok.Data;

@Data
public abstract class PinyinStyle
{
    protected int num = 1;
    /*
    * 标注音调方式
    * 0 - 不加
    * 1 - 默认添加
    * 其他 - 特殊
    * */
    protected int capital = 0;
    /*
    * 大小写
    * 0 - 全部小写
    * 1 - 全部大写
    * 2 - 首字母大写
    * */
}
