package com.shuowen.yuzong.data.domain.Pinyin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import lombok.Data;

import java.util.*;

@Data
public class PinyinDetail
{
    @JsonInclude (JsonInclude.Include.NON_NULL)
    String attribute;
    @JsonInclude (JsonInclude.Include.NON_NULL)
    String standard;
    @JsonInclude (JsonInclude.Include.NON_NULL)
    String keyboard;
    boolean valid;

    private PinyinDetail(String attribute, String standard, String keyboard, boolean valid)
    {
        this.standard = standard;
        this.keyboard = keyboard;
        this.valid = valid;
        this.attribute = attribute;
    }

    public static PinyinDetail of(String attribute, String standard, String keyboard)
    {
        return new PinyinDetail(attribute, standard, keyboard, true);
    }

    public static PinyinDetail exist(String standard, String keyboard)
    {
        return new PinyinDetail("tone", standard, keyboard, true);
    }

    public static PinyinDetail notExist()
    {
        return new PinyinDetail(null, null, null, false);
    }

    public static List<PinyinDetail> listOf(String attribute, String standard, String keyboard)
    {
        String[] standardArray = standard.split("\\|");
        String[] keyboardArray = keyboard.split("\\|");

        int len = standardArray.length;
        if (!ObjectTool.allEqual(standardArray.length, keyboardArray.length))
            throw new IllegalArgumentException("数量不一样");

        List<PinyinDetail> list = new ArrayList<>();
        for (int i = 0; i < len; i++)
        {
            standardArray[i]=" ["+standardArray[i]+"] ";
            keyboardArray[i]=" ["+keyboardArray[i]+"] ";
            list.add(PinyinDetail.of(attribute, standardArray[i], keyboardArray[i]));
        }
        return list;
    }
}
