package com.shuowen.yuzong.data.dto.Character;

import lombok.Data;

/**
 * 用于在编辑的时候多个词条粗筛
 *
 * @see com.shuowen.yuzong.controller.edit.EditNamController
 * @apiNote 获得了这个列表，选中一个把id发回来
 */

@Data
public class HanziOutline
{
    Integer id;
    String hanzi;
    String hantz;
    String stdPy;
}
