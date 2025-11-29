package com.shuowen.yuzong.Linguistics.Mandarin;


import com.hankcs.hanlp.*;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.hankcs.hanlp.dictionary.py.PinyinDictionary;

import java.util.*;

import static com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool.replace;

/**
 * 所有汉语拼音有关内容都放在这里， Pinyin类不要用在外面，外面直接用字符串接住
 * @apiNote 由于Hanlp使用5表示轻声，这里不做修改，0声是无效的
 * */

public class HanPinyin
{
    /**
     *字转拼音数组：一段这样的文字，自動判斷多音字 -> [yi1, duan4, zhe4, yang4, de5, wen2, zi4]
     *  */
    public static List<String> txtPinyin(String txt)
    {
        List<Pinyin> a = HanLP.convertToPinyinList(txt);
        List<String> ans = new ArrayList<>();
        if (a.size() != txt.length()) throw new RuntimeException("数量不对应");
        for (int i = 0; i < a.size(); i++)
        {
            switch (txt.substring(i, i + 1))
            {
                case "兀" -> ans.add("wu4");
                case "嗀" -> ans.add("hu4");
                default -> ans.add(a.get(i).toString());
            }
        }
        return ans;
    }

    /**
     * 字转拼音数组：行 ->[xing2, hang2]
     * */
    public static List<String> toPinyin(String c)//看上去是字符串，但只调用一个字
    {
        // hanlp的小bug，读音标不出来
        if("兀".equals(c)) return List.of("wu4"); //组词没有问题
        if("嗀".equals(c)) return List.of("hu4","gu3");

        Pinyin[] ans = PinyinDictionary.get(c);
        List<String> py=new ArrayList<>();
        if(ans==null)
        {
            py.add("none5");
            return py;
        }
        for(Pinyin i:ans)
        {
            py.add(i.toString());
        }
        return py;
    }



    /**
     * 拼音去除标号  zhe4->zhe
     * */
    public static String toSyllable(String string)
    {
        if(string.equals("none5")) return null;

        StringBuilder str = new StringBuilder(string);
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    /**
     * 拼音轉注音標號  zhe4->4
     * @apiNote 无效的时候使用-1做返回值
     * */
    public static int toTone(String str)
    {
        if (str.length() <= 1) return -1;

        int ans = str.charAt(str.length() - 1) - '0';
        return (ans >= 1 && ans <= 5) ? ans : -1;
    }

    /**
     * 一个电脑处理的拼音变常见拼音，把{@code pin1} 转换成{@code pīn}
     * @apiNote 无效的时候使用空字符串做返回值
     **/
    public static String topMark(String pinyin)
    {
        StringBuilder str = new StringBuilder(pinyin);
        str.deleteCharAt(str.length() - 1);

        int tongue = toTone(pinyin);

        if ("none".contentEquals(str)||tongue == -1) return "";

        replace(str, "v", "ü");

        /* 使用的算法是基于简化描述：
         *
         * 1. 有iu组合时标记在u上，有ui组合时标记在i上
         * 2. 依照a o e i u ü 的顺序标记在最先出现的字母上
         * */

        // 二维查找：map.get(元音).get(声调)
        Map<String, List<String>> map = Map.of(
                "a", List.of("", "ā", "á", "ǎ", "à", "a"),
                "o", List.of("", "ō", "ó", "ǒ", "ò", "o"),
                "e", List.of("", "ē", "é", "ě", "è", "e"),
                "i", List.of("", "ī", "í", "ǐ", "ì", "i"),
                "u", List.of("", "ū", "ú", "ǔ", "ù", "u"),
                "ü", List.of("", "ǖ", "ǘ", "ǚ", "ǜ", "ü")
        );

        if (str.indexOf("iu") != -1)
        {
            replace(str, "u", map.get("u").get(tongue));
        }
        else if (str.indexOf("ui") != -1)
        {
            replace(str, "i", map.get("i").get(tongue));
        }
        else
        {
            for (String i : List.of("a", "o", "e", "i", "u", "ü"))
            {
                if (str.indexOf(i) != -1)
                {
                    replace(str, i, map.get(i).get(tongue));
                    break;
                }
            }
        }
        return str.toString();
    }
}