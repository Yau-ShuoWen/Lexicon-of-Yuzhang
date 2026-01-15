package com.shuowen.yuzong.Linguistics.Mandarin;

import com.hankcs.hanlp.*;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.hankcs.hanlp.dictionary.py.PinyinDictionary;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;

import java.util.*;

/**
 * 所有汉语拼音有关内容都放在这里， Pinyin类不要用在外面，外面直接用字符串接住，
 * 改掉了一些不想要的形式，比如 {@code none5} 的写法和  {@code 5} 作为轻声，
 * 顺便为两个常用字补丁一个读音。
 */
public class HanPinyin
{
    public static String INVALID = "-";

    /**
     * Hanlp使用5表示轻声，这里还是把他改成0。
     */
    private static String HanlpPinyinToString(Pinyin py)
    {
        String ans = py.toString();
        if ("none5".equals(ans)) return INVALID;
        if (ans.endsWith("5")) ans = ans.substring(0, ans.length() - 1) + "0";
        return ans;
    }

    /**
     * 字转拼音数组：一段这样的文字，自動判斷多音字 -> [yi1, duan4, zhe4, yang4, de5, wen2, zi4]
     */
    public static List<String> textPinyin(String text)
    {
        List<Pinyin> pinyin = HanLP.convertToPinyinList(text);
        List<String> ans = new ArrayList<>();
        if (pinyin.size() != text.length()) throw new RuntimeException("数量不对应");
        for (int i = 0; i < pinyin.size(); i++)
        {
            ans.add(switch (text.charAt(i))
            {
                case '兀' -> "wu4";
                case '嗀' -> "hu4";
                default -> HanlpPinyinToString(pinyin.get(i));
            });
        }
        return ans;
    }

    /**
     * 字转拼音数组：行 ->[xing2, hang2]
     */
    public static List<String> toPinyinList(String c)//看上去是字符串，但只调用一个字
    {
        // hanlp的小bug，读音标不出来
        if ("兀".equals(c)) return List.of("wu4");  //组词没有问题
        if ("嗀".equals(c)) return List.of("hu4", "gu3");

        Pinyin[] py = PinyinDictionary.get(c);
        if (py == null) return List.of();
        else
        {
            List<Pinyin> pinyin = new ArrayList<>(List.of(PinyinDictionary.get(c)));
            return ListTool.mapping(pinyin, HanPinyin::HanlpPinyinToString);
        }
    }

    /**
     * 标准的拼音去除标号（没有安全检查）
     *
     * @return 传入无效拼音"-"返回""
     */
    public static String toSyllable(String string)
    {
        if (INVALID.equals(string)) return "";
        return string.substring(0, string.length() - 1);
    }

    /**
     * 拼音提取音调（没有安全检查）
     *
     * @return 字符串长度小于等于1，或者返回超过[0,4]，返回-1
     */
    public static int toTone(String string)
    {
        if (string.length() <= 1) return -1;  //无效的字符串正好也是长度小于1的

        int ans = string.charAt(string.length() - 1) - '0';
        return (ans >= 0 && ans <= 4) ? ans : -1;
    }

    /**
     * 把使用数字标注音调的读音，把{@code pin1} 转换成{@code pīn}，没有安全检查，
     * 因为默认是和{@code textPinyin} {@code toPinyinList} 的结果一起使用：
     *
     * <blockquote><pre>
     * ListTool.mapping(HanPinyin.textPinyin("一段这样的文字"), HanPinyin::topMark)
     * ListTool.mapping(HanPinyin.toPinyinList("行"), HanPinyin::topMark)
     * </pre></blockquote>
     *
     * <p>
     * 使用的算法是基于简化描述：<br>
     * 1. 依照 {@code a > o > e > i > u > ü} 的顺序标记在最先出现的字母上<br>
     * 2. 例外的只有当出现 {@code iu/ui}组合时标记在后一个字母上（而{@code ui}也符合{@code i > u}，所以不用单独拿出来）
     *
     * @return 无效的时候使用空字符串做返回值
     **/
    public static String topMark(String pinyin)
    {
        if (INVALID.equals(pinyin)) return "";

        int tongue = toTone(pinyin);
        if (tongue == -1) return "";
        pinyin = toSyllable(pinyin).replace("v", "ü");

        // 二维查找：map.get(元音)[声调]
        Map<String, String[]> map = Map.of(
                "a", "aāáǎà".split(""),
                "o", "oōóǒò".split(""),
                "e", "eēéěè".split(""),
                "i", "iīíǐì".split(""),
                "u", "uūúǔù".split(""),
                "ü", "üǖǘǚǜ".split("")
        );

        // iu是特殊规则的例外，改变后直接返回
        if (pinyin.contains("iu")) return pinyin.replace("u", map.get("u")[tongue]);
        // 通常情况按照顺序识别
        for (String i : "aoeiuü".split(""))
            if (pinyin.contains(i)) return pinyin.replace(i, map.get(i)[tongue]);
        // 例外直接返回（应该没有例外）
        return pinyin;
    }
}