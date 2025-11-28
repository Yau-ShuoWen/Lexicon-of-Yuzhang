package com.shuowen.yuzong.data.model.Character;

import lombok.Data;

/**
 * 普通话和方言连接的类
 */

// 查询用不到那么多信息，什么id什么的，所以简化为两个
// TODO 之后可以新增多音字选择 mulpy 的内容

@Data
public class DialectChar
{
    String info;  // 普通话读音和信息
    String stdPy; // 标准拼音
}
