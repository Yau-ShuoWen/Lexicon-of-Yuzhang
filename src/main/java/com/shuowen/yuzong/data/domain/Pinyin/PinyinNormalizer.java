package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;

public class PinyinNormalizer
{
    /**
     * 尝试将一个字符串拆成声母和声调，如果没有音调补0
     */
    public static Pair<String, Integer> trySplit(String text)
    {
        StringTool.checkTrimValid(text); // 如果是空的，取最后一个会报错

        char ch = StringTool.back(text);
        if (NumberTool.closeBetween(ch, '0', '9'))
        {
            text = StringTool.deleteBack(text);
            return Pair.of(text, ch - '0');
        }
        else return Pair.of(text, 0);
    }

    protected static String filterNam(String text)
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
