package com.shuowen.yuzong.dict.hanzi.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** 列表批量读取时附带字形，不改变普通话候选接口的返回结构。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MdrCharWithHanzi extends MdrChar
{
    private String hanzi;
}
