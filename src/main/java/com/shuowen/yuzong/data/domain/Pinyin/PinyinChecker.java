package com.shuowen.yuzong.data.domain.Pinyin;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;

/**
 * 拼音检查器
 */
public class PinyinChecker
{
    /**
     * 提示性地识别拼音正确性
     *
     * @return 返回三元组
     * <br>1. 正确  {@code (1, 拼音预览, 空)}
     * <br>2. 错了，但是被模糊识别了 {@code (2, 正确拼音预览 , 正确写法)}
     * <br>3. 完全识别不了 {@code (3, 空, 空)}
     * <br>4. 需要补充音调 {@code (4, 空, 空)}
     */
    public static Triple<Integer, String, String> suggestively(String text, Dialect d)
    {
        StringTool.checkTrimValid(text);

        if (!NumberTool.closeBetween(StringTool.back(text), '0', '9'))
            return Triple.of(4, "", "");

        var rawPinyinAnswer = d.tryCreatePinyin(text);
        var newPinyinAnswer = d.tryCreatePinyin(d.normalizePinyin(text));

        // 如果修正格式的有效，那么相等就是对的（1），不等的就是被修复的（2）
        // 如果修正格式的也无效，相当于救不回来了，无效（3）
        if (newPinyinAnswer.isValid())
        {
            var newPinyin = newPinyinAnswer.getValue();
            if (Maybe.allValidAndEqual(rawPinyinAnswer, newPinyinAnswer))
            {
                return Triple.of(1, PinyinFormatter.handle(newPinyin, d), "");
            }
            else
            {
                String trueAns = PinyinFormatter.handle(newPinyin, d, PinyinParam.of(Scheme.KEYBOARD));
                return Triple.of(2, PinyinFormatter.handle(newPinyin, d), trueAns.substring(2, trueAns.length() - 2));
            }
        }
        else return Triple.of(3, "", "");
    }

    /**
     * 用于前端传送到后端的拼音检查
     * <br>使用场景：有了校对工具，如果再出现不正确的，那就报错回前端
     */
    public static void strictly(String text, Dialect d)
    {
        if (suggestively(text, d).getLeft() != 1) throw new IllegalArgumentException(text + "拼音不符合格式");
    }
}
