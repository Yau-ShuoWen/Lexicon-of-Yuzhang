package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;

import java.util.*;
import java.util.function.Function;

/**
 * 静态拼音拼音处理类
 *
 * @implNote 因为内部有完善的机制处理null等情况，所以这个工具类本身不需要检查
 */
public class PinyinTool
{
    /**
     * 对某一个方言，指定某种格式化方法的拼音格式化
     */
    public static String formatPinyin(String py, Dialect d, PinyinParam param)
    {
        return d.createPinyin(py).toString(d.createStyle(param));
    }

    /**
     * 对某一个方言，指定一系列格式化方法的拼音格式化
     */
    public static String formatPinyin(String py, Dialect d, PinyinParam... param)
    {
        return String.join(" | ", ListTool.mapping(List.of(param), i -> formatPinyin(py, d, i)));
    }

    /**
     * 对某一个方言指定，一个专业格式化方法的拼音格式化
     */
    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    String formatPinyin(String py, Function<String, T> pinyinCreator, U style)
    {
        return pinyinCreator.apply(py).toString(style);
    }

    /**
     * 静态方法：渲染使用一对分隔符（如[]）包围的字符串，并且按照原来的顺序转化
     *
     * @implNote {@code [fung1][qieu2][ia5][pok6]}转换为{@code  //fung1  qieu2  ia5  pok6// }
     */
    public static <P extends UniPinyin<S>, S extends PinyinStyle>
    String parseAndReplace(String str, Function<String, P> pinyinCreator, S style, String start, String end)
    {
        StringBuilder res = new StringBuilder();
        int i = 0;

        while (true)
        {
            int open = str.indexOf(start, i);
            if (open == -1)
            {
                res.append(str.substring(i));
                break;
            }
            int close = str.indexOf(end, open + start.length());
            if (close == -1)
            {
                res.append(str.substring(i));
                break;
            }
            res.append(str, i, open);
            res.append(formatPinyin(str.substring(open + start.length(), close), pinyinCreator, style));
            i = close + end.length();
        }

        return res.toString().replace("]  [", "  ");
    }
}