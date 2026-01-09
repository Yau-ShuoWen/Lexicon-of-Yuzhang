package com.shuowen.yuzong.data.dto.Character;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.data.model.Character.HanziEntity;
import lombok.Data;

import java.util.*;

/**
 * 用于在编辑的时候多个词条粗筛
 *
 * @apiNote 获得了这个列表，选中一个把id发回来
 */
@Data
public class HanziOutline
{
    Integer id;
    String sc;
    String tc;
    String mainPy;

    public HanziOutline()
    {
    }

    /**
     * 这里使用原始值直接转换是因为需要保证原来的一条还是一条
     */
    public HanziOutline(HanziEntity ch)
    {
        id = ch.getId();
        sc = ch.getSc();
        tc = ch.getTc();
        mainPy = ch.getMainPy();
    }

    public static List<HanziOutline> listOf(List<HanziEntity> ch)
    {
        return ListTool.mapping(ch, HanziOutline::new);
    }
}
