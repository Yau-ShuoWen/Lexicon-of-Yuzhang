package com.shuowen.yuzong.data.dto.Character;

import com.shuowen.yuzong.data.model.Character.CharEntity;
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
    String hanzi;
    String hantz;
    String stdPy;

    public HanziOutline()
    {
    }

    /**
     * 这里使用原始值直接转换是因为需要保证原来的一条还是一条
     */
    public HanziOutline(CharEntity ch)
    {
        id = ch.getId();
        hanzi = ch.getHanzi();
        hantz = ch.getHantz();
        stdPy = ch.getStdPy();
    }

    public static List<HanziOutline> listOf(List<CharEntity> ch)
    {
        List<HanziOutline> ans = new ArrayList<>();
        for (CharEntity i : ch) ans.add(new HanziOutline(i));
        return ans;
    }
}
