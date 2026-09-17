package com.shuowen.yuzong.dict.hanzi.model;

import lombok.Data;

/** 普通话读音候选 DTO；映射关系保存在汉字拼音 JSON 中。 */
@Data
public class MdrChar
{
    String info;     // 普通话的读音信息
    Integer mandarinId;
}
