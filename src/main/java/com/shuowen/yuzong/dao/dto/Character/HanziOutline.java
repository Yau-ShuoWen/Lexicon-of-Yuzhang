package com.shuowen.yuzong.dao.dto.Character;

import lombok.Data;

/**
 * 用语在编辑的时候多个词条选择是哪一个用的
 *
 * @see com.shuowen.yuzong.controller.search.ResultNamController
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
