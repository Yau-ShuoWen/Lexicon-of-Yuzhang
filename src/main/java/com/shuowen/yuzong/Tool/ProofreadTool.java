package com.shuowen.yuzong.Tool;

import com.hankcs.hanlp.HanLP;
import com.shuowen.yuzong.Tool.DataVersionCtrl.UStringCompareUtil;
import com.shuowen.yuzong.Tool.JavaUtilExtend.MapTool;
import com.shuowen.yuzong.Tool.TextTool.Punctuation;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;

import java.util.*;

/**
 * 一个古籍简繁和格式管理工具类
 *
 * @see com.shuowen.yuzong.Tool.DataVersionCtrl.UStringCompareUtil
 * @see com.shuowen.yuzong.Tool.dataStructure.UString
 */
public class ProofreadTool
{
    private ProofreadTool()
    {
    }

    /**
     * 把全角引号更换成直角引号
     */
    public static String handleQuotationMark(String text)
    {
        return text.replace("“", "「").replace("”", "」");
    }

    /**
     * 使用HanLP库机翻
     */
    public static String useHanlpTranslate(String text, Language from)
    {
        return from.isSimplified() ? HanLP.s2t(text) : HanLP.t2s(text);
    }

    /**
     * 少数方言正字需要保留不要转换
     */
    public static UString escapeCharTraslate(UString text, Language from, final OrthoCharset charset)
    {
        // 先机翻，然后立刻转换成统一码字符串
        var tmp = UString.of(useHanlpTranslate(text.toString(), from));

        // 是否更换的标准取决于charset规则
        var ans = UString.of();
        for (int i=0;i<text.length();i++)
        {
            ans.append(charset.choose(text.uCharAt(i),tmp.uCharAt(i)));
        }
        return ans;
    }

    /**
     * 在不干扰不变区域的情况下对修改部分新增，防止全量更新导致的已经编辑的简体字部分在繁体更新后被覆盖
     */
    public static Map<String, UString> retainContextTranslate(
            UString oldTc, UString newTc, UString oldSc, final OrthoCharset charset)
    {
        // 长度相等才能共用索引
        if (oldTc.length() != oldSc.length()) throw new IllegalArgumentException("旧字符串长度不等");

        // 在方言的基础上加上标点符号的规则
        charset.addIgnores(Punctuation.getCharset());

        var newScTmp = escapeCharTraslate(newTc, Language.TC, charset);
        var changes = UStringCompareUtil.compare(oldTc, newTc);
        var newSc = UString.of();

        /* 插入：直接newScTmp 相同位置插入
         * tc 南昌在 -> 南昌县在  idx=(2,2)  newScTmp 南昌'縣'在  南昌在->   南昌'縣'在
         *
         * 刪除：直接刪除
         * tc 滕王閣序是 -> 滕王閣是  idx=(3,3)  不涉及newScTmp  滕王阁序是->滕王阁''是
         *
         * 修改：删除加插入
         * tc 我去南昌 -> 我來南昌  idx=(1,1) newScTmp 我'来'南昌  我在南昌 ->我来南昌
         *
         * 不变：直接拼接
         * */
        int idx = 0;
        for (var i : changes)
        {
            Twin<Integer> oldR = i.getOldItem();
            Twin<Integer> newR = i.getNewItem();

            int start = oldR.getLeft();

            if (start > idx)
            {
                newSc.append(oldSc.substring(idx, start));
                idx = start;
            }

            switch (i.getChangeType())
            {
                // 增加，从「翻译字符串」取出内容新增，指针idx不变
                case ADDED -> newSc.append(newScTmp.substring(newR.getLeft(), newR.getRight()));

                // 删除，指针跳过 oldSc 中被删除的部分
                case DELETED -> idx = oldR.getRight();

                // 更改，两者之和
                case MODIFIED ->
                {
                    newSc.append(newScTmp.substring(newR.getLeft(), newR.getRight()));
                    idx = oldR.getRight();
                }
            }
        }
        if (idx < oldSc.length()) newSc.append(oldSc.substring(idx, oldSc.length()));

        if (newSc.length() != newTc.length()) throw new RuntimeException(String.format("""
                算法出现错误，生成的新的简体字符串字数不等于繁体。
                原来繁体：%s
                原来简体：%s
                新繁体：%s
                新简体：%s
                获得的临时简体字符串：%s
                """, oldTc, oldSc, newTc, newSc, newScTmp));

        return MapTool.orderMapOf("sc", newSc, "tc", newTc);
    }
}
