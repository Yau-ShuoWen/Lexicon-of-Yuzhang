package com.shuowen.yuzong.dao.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.dataStructure.functions.TriFunction;
import com.shuowen.yuzong.dao.domain.IPA.IPASyllableStyle;
import com.shuowen.yuzong.dao.domain.IPA.IPAToneStyle;

import java.util.*;
import java.util.function.Function;

public class PinyinTool
{
    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    Map<String, String> formatPinyin(Set<String> py, U style, Function<String, T> creator)
    {
        Map<String, String> map = new HashMap<>();
        for (String i : py)
            map.put(i, creator.apply(i).toString(style));
        return map;
    }

    public static <T extends UniPinyin<U>, U extends PinyinStyle>
    Map<String, Map<String, String>> formatIPA(Set<String> py, Function<String, T> creator,
                                               TriFunction<Set<T>, IPAToneStyle, IPASyllableStyle, Map<T, Map<String, String>>> ipaSE,
                                               IPAToneStyle ts, IPASyllableStyle ss)
    {
        Set<T> pySet = new HashSet<>();
        Map<String, Map<String, String>> res = new HashMap<>();

        for (String i : py) pySet.add(creator.apply(i));
        Map<T, Map<String, String>> data = ipaSE.apply(pySet, ts, ss);
        for (String i : py)
            res.put(i, data.get(creator.apply(i)));

        return res;
    }


    /**
     * 静态方法：字符串快速格式化为拼音
     */
    public static <P extends UniPinyin<S>, S extends PinyinStyle>
    String formatting(String s, Function<String, P> creator, S style)
    {
        // 因为内部有完善的机制处理null等情况，所以这里不需要检查
        // 所以直接创建并且按照style格式化即可
        return creator.apply(s).toString(style);
    }

    /**
     * 静态方法：一列字符串读取为一个拼音的数组，之间使用分隔符
     */
    public static <P extends UniPinyin<S>, S extends PinyinStyle>
    List<P> toPinyinList(String s, Function<String, P> creator, String separator)
    {
        if (s == null || s.trim().isEmpty()) return new ArrayList<>();

        String[] arr = s.split(separator);
        List<P> list = new ArrayList<>();
        for (String str : arr)
        {
            if (!str.trim().isEmpty())
            {
                P pinyin = creator.apply(str.trim());
                list.add(pinyin);
            }
        }
        return list;
    }

    /**
     * 静态方法：拼音数组格式化为字符串数组
     */
    public static <P extends UniPinyin<S>, S extends PinyinStyle>
    List<String> toList(List<P> list, S style)
    {
        List<String> ans = new ArrayList<>();
        for (P i : list) ans.add(i.toString(style));
        return ans;
    }

    /**
     * 静态方法渲染：渲染使用一种分隔符（如空格）包围的字符串，并且按照原来的顺序转化
     *
     * @implNote {@code fung1 qieu2 ia5 pok6}转换为{@code  //fung1  qieu2  ia5  pok6// }
     */
    public static <P extends UniPinyin<S>, S extends PinyinStyle>
    String splitAndReplace(String s, Function<String, P> creator, S style, String separator)
    {
        var list = toPinyinList(s, creator, separator);

        StringBuilder sb = new StringBuilder();
        for (P i : list) sb.append(i.toString(style) + separator);
        return sb.toString().replace("//   //", "  ");
    }

    /**
     * 静态方法：渲染使用一对分隔符（如[]）包围的字符串，并且按照原来的顺序转化
     *
     * @implNote {@code [fung1][qieu2][ia5][pok6]}转换为{@code  //fung1  qieu2  ia5  pok6// }
     */
    public static <P extends UniPinyin<S>, S extends PinyinStyle>
    String parseAndReplace(String str, Function<String, P> creator, S style, String start, String end)
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
            res.append(formatting(str.substring(open + start.length(), close), creator, style));
            i = close + end.length();
        }

        return res.toString().replace("//  //", "  ");
    }


}