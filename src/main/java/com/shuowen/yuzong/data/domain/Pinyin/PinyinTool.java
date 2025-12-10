package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;

import java.util.*;
import java.util.function.Function;

/**
 * 静态拼音拼音处理类
 */
public class PinyinTool
{
    /**
     * 静态方法：拼音字符串变风格
     *
     * @param py            拼音字符串
     * @param pinyinCreator 拼音对象的构造函数
     * @param style         拼音格式
     */
    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    String formatPinyin(String py, Function<String, T> pinyinCreator, U style)
    {
        // 因为内部有完善的机制处理null等情况，所以这里不需要检查
        // 所以直接创建并且按照style格式化即可
        return pinyinCreator.apply(py).toString(style);
    }

    /**
     * 批量处理列表
     */
    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    List<String> formatPinyin(List<String> py, Function<String, T> pinyinCreator, U style)
    {
        List<String> res = new ArrayList<>();
        for (String s : py) res.add(formatPinyin(s, pinyinCreator, style));
        return res;
    }

    /**
     * 批量渲染集合，获得对应键值对，可以反复使用
     */
    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    Map<String, String> formatPinyin(Set<String> py, Function<String, T> pinyinCreator, U style)
    {
        Map<String, String> map = new HashMap<>();
        for (String s : py) map.put(s, formatPinyin(s, pinyinCreator, style));
        return map;
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