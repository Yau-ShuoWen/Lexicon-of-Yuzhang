package com.shuowen.yuzong.Tool.TextTool;

import com.shuowen.yuzong.Tool.JavaUtilExtend.SetTool;
import com.shuowen.yuzong.Tool.dataStructure.UChar;

import java.util.*;

/**
 * 标点符号类
 */
public class Punctuation
{
    private Punctuation()
    {
    }

    /**
     * 对HanLP的一个补丁，在繁转简的时候，会把{@code 「」}转换成{@code “”}，这是为了在规则里加上忽略
     * */
    public static Set<UChar> getCharset()
    {
        var set = Set.of("「", "」");
        return SetTool.mapping(set, UChar::of);
    }

    public static String fullWidth(String s)
    {
        return s.
                replace(",", "，").
                replace(".", "。").
                replace("?", "？").
                replace("!", "！").
                replace("...", "……").
                replace(":", "：").
                replace(";", "；").
                replace("(", "（").
                replace(")", "）").
                replace("·", " · ");
    }

    public static String halfWidth(String s)
    {
        return s.
                replace("【", "[").
                replace("】", "]");
    }
}
