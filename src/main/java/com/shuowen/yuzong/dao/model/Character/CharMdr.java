package com.shuowen.yuzong.dao.model.Character;

import lombok.Data;

/**
 * 普通话读法和映射类
 * @apiNote 这个类在前端和后端都绝对不能改变，
 * 前端要修改应该是以整个MdeChar为一体的从后端拿出来加入列表
 */

@Data
public class CharMdr
{
    String info;     // 普通话的读音信息
    Integer leftId;  // 映射表里的普通话外键
    Integer rightId; // 映射表里的方言外键
}
