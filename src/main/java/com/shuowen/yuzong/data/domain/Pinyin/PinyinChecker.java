package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;

import java.util.*;

public class PinyinChecker
{
    /**
     * <ol>
     *     <li>正确  {@code (1, 拼音预览, 空)}</li>
     *     <li>错了，但是被模糊识别了 {@code (2 , 正确拼音预览 , 正确写法)}</li>
     *     <li>完全识别不了 {@code (3 , 空, 空 )}</li>
     * </ol>
     */
    public static Triple<Integer, String, String> check(String text, Dialect d)
    {
        StringTool.checkTrimValid(text);

        var pinyin = d.createPinyin(text);

        if (pinyin.isValid()) return Triple.of(1, pinyin.toString(), "");
        else
        {
            text = text.toLowerCase();
            text = switch (d)
            {
                case NAM -> checkNam(text);
            };
            pinyin = d.createPinyin(text);

            if (pinyin.isValid())
            {
                String trueAns = pinyin.toString(d.createStyle(PinyinParam.of(Scheme.KEYBOARD)));
                trueAns = trueAns.trim().replace("[", "").replace("]", "");
                return Triple.of(2, pinyin.toString(), trueAns);
            }
            else return Triple.of(3, "", "");
        }
    }

    /**
     * 用于前端传送到后端的拼音检查，有了校对工具，如果再出现不正确的，那就报错回前端
     */
    public static void checkStrictly(String text, Dialect d)
    {
        if (check(text, d).getLeft() != 1)
            throw new IllegalArgumentException(text + "拼音不符合格式");
    }

    /**
     * 尝试将一个字符串拆成声母和声调
     */
    public static Pair<String, Integer> trySplit(String text)
    {
        StringTool.checkTrimValid(text); // 如果是空的，取最后一个会报错

        char ch = text.charAt(text.length() - 1);
        if (ch >= '0' && ch <= '9')
        {
            text = text.substring(0, text.length() - 1);
            return Pair.of(text, ch - '0');
        }
        else
        {
            return Pair.of(text, 0);
        }
    }

    private static String checkNam(String text)
    {
        text = text.toLowerCase();

        var tmp = trySplit(text);
        text = tmp.getLeft();
        int tone = tmp.getRight();

        /* 过滤原则
         * 1.ü可以写作yu，数据库层是v
         * 2.ẹ可以写作ee，数据库层也是ee
         * */
        final List<Pair<String, String>> ruleReplace = List.of(
                Pair.of("v", "yu"),
                Pair.of("ü", "yu"),
                Pair.of("ẹ", "ee")
        );


        /* 过滤原则：
         * 1.零声母的y或w：规范化为i或u
         * 2.jqx开头的u：规范化为v
         * */
        final List<Pair<String, String>> ruleBegin = List.of(
                Pair.of("yi", "i"),
                Pair.of("wu", "u"),
                Pair.of("w", "u"),
                Pair.of("y", "i"),
                Pair.of("ju", "jv"),
                Pair.of("qu", "qv"),
                Pair.of("xu", "xv"),
                Pair.of("fi", "feei")
        );

        /* 过滤原则：
         * 1.韵母为ao：规范化为au
         * 2.iv后的an：规范化为en
         * 3.普通话常用读音修改：wei->uei（上一部）->ui you->iou->iu wen->uen->un
         * 4.zcs后面没有加足两个i：补全
         * */
        final List<Pair<String, String>> ruleEnd = List.of(
                Pair.of("ao", "au"),
                Pair.of("ian", "ien"),
                Pair.of("van", "von"),
                Pair.of("uen", "un"),
                Pair.of("uei", "ui"),
                Pair.of("iou", "ieu"),
                Pair.of("zi", "zii"),
                Pair.of("ci", "cii"),
                Pair.of("si", "sii"),
                Pair.of("z", "zii"),
                Pair.of("c", "cii"),
                Pair.of("s", "sii")
        );

        for (var p : ruleReplace)
        {
            text = text.replace(p.getLeft(), p.getRight());
        }

        for (var p : ruleBegin)
        {
            if (text.startsWith(p.getLeft()))
            {
                text = p.getRight() + text.substring(p.getLeft().length());
                break;
            }
        }
        for (Pair<String, String> p : ruleEnd)
        {
            if (text.endsWith(p.getLeft()))
            {
                text = text.substring(0, text.length() - p.getLeft().length()) + p.getRight();
                break;
            }
        }
        return text + tone;
    }
}
