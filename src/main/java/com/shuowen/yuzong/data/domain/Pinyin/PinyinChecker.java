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
     * @return <br>1. 正确  {@code (1, 拼音预览, 空)}
     * <br>2. 错了，但是被模糊识别了 {@code (2 , 正确拼音预览 , 正确写法)}
     * <br>3. 完全识别不了 {@code (3 , 空, 空 )}
     * <br>4. 需要补充音调 {@code (4 , 空, 空 )}
     */
    public static Triple<Integer, String, String> check(String text, Dialect d)
    {
        StringTool.checkTrimValid(text);

        if (!haveTone(text)) return Triple.of(4, "", "");

        var rawPinyin = d.createPinyin(text);
        String newText = text.toLowerCase();
        newText = switch (d)
        {
            case NAM -> filterNam(newText);
        };
        var newPinyin = d.createPinyin(newText);

        // 如果修正格式的有效，那么相等就是对的（1），不等的就是被修复的（2）
        // 如果修正格式的也无效，相当于救不回来了，无效（3）
        if (newPinyin.isValid())
        {
            if (Objects.equals(rawPinyin, newPinyin)) return Triple.of(1, PinyinTool.formatPinyin(rawPinyin, d), "");
            else
            {
                String trueAns = newPinyin.toString(d.createStyle(PinyinParam.of(Scheme.KEYBOARD))).
                        replace("[", "").replace("]", "").replace(" ", "");
                return Triple.of(2, PinyinTool.formatPinyin(newPinyin, d), trueAns);
            }
        }
        else return Triple.of(3, "", "");
    }

    /**
     * 用于前端传送到后端的拼音检查，有了校对工具，如果再出现不正确的，那就报错回前端
     */
    public static void checkStrictly(String text, Dialect d)
    {
        if (check(text, d).getLeft() != 1)
            throw new IllegalArgumentException(text + "拼音不符合格式");
    }

    public static boolean haveTone(String text)
    {
        StringTool.checkTrimValid(text); // 如果是空的，取最后一个会报错
        char ch = text.charAt(text.length() - 1);
        return ch >= '0' && ch <= '9';
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

    private static String filterNam(String text)
    {
        // 《智能是一个巨大的if-else语句》
        text = text.toLowerCase();

        var tmp = trySplit(text);
        text = tmp.getLeft();
        int tone = tmp.getRight();

        // 处理 i 在开头：
        // 匹配：y开头，但不是yu
        // 写法处理：yi->i  yit->it  ya->ia
        if (text.matches("^y[^u].*")) text = text.replace("yi", "i").replace("y", "i");

        // 处理 u 在开头：
        // 匹配：w开头
        // 写法处理：wu->u  wut->ut  wa->ua
        if (text.matches("^w.*")) text = text.replace("wu", "u").replace("w", "u");

        // 处理 ü 在字符串中：
        // 匹配：字符串包含v或者ü
        // 写法处理：lve-> lyue  ün->yun
        if (text.matches(".*[vü].*")) text = text.replace("v", "yu").replace("ü", "yu");

        // 处理 ju qu xu：
        // 匹配，第一个字符是jqx，第二个字母是u
        // 写法处理：ju->jyu   que->qyue   xuen->xyuen
        if (text.matches("^([jqx])u.*")) text = text.replace("u", "yu");

        // 单独处理 feei
        if (text.equals("fi")) text = text.replace("fi", "feei");

        // 处理 ẹ
        if (text.contains("ẹ")) text = text.replace("ẹ", "ee");
        if (text.contains("ọ")) text = text.replace("ọ", "oe");

        // 处理 zii cii sii 后面i不足量的问题：
        // 匹配：第一个字母是zcs，没有或有一个i，然后立刻结束字符串
        // 写法处理：z->zii   ci->cii   s->sii
        if (text.matches("^[zcs]i?$")) text = text.charAt(0) + "ii";

        // 双韵母的模糊处理
        // 匹配：普通话常见但是不符合的： ao iao ou iou uei
        // 特殊：iou/uei的简写歪打正着iu/ui，这里的iou/uei实际上是从you/wei变过来的
        // ao->au  iau->ieu  ou->eu  iou->iu
        if (text.contains("ao")) text = text.replace("ao", "au");
        if (text.contains("iau")) text = text.replace("iau", "ieu");
        if (text.contains("ou"))
        {
            if (text.contains("iou"))
                text = text.replace("iou", "iu");
            else text = text.replace("ou", "eu");
        }
        if (text.contains("uei")) text = text.replace("uei", "ui");

        // 鼻韵母的模糊处理
        // 匹配：普通话常见但是不符合的： ian yuan uen
        // 特殊：uen的简写歪打正着un，这里的uen实际上是从wen变过来的
        // ian->ien  yuan->yuon  uen->un
        if (text.contains("ian")) text = text.replace("ian", "ien");
        if (text.contains("yuan")) text = text.replace("yuan", "yuon");
        if (text.contains("uen")) text = text.replace("uen", "un");

        return text + tone;
    }
}
