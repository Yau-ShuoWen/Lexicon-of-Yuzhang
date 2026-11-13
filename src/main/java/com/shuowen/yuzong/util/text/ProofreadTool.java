package com.shuowen.yuzong.util.text;

import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.tuple.Twin;
import com.shuowen.yuzong.util.version.UStringCompareUtil;

/**
 * 一个古籍简繁和格式管理工具类
 *
 * @see UStringCompareUtil
 * @see UString
 */
public class ProofreadTool
{
    private ProofreadTool()
    {
    }

    /**
     * 在不干扰不变区域的情况下对修改部分新增，防止全量更新导致的已经编辑的简体字部分在繁体更新后被覆盖
     */
    public static ScTcText retainContextTranslate(
            UString oldTc, UString newTc, UString oldSc, Dialect d)
    {
        // 长度相等才能共用索引
        if (oldTc.length() != oldSc.length()) throw new IllegalArgumentException("旧字符串长度不等");

        var newScTmp = Translate.toSc(newTc, d);
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

        return new ScTcText(newTc, newSc);
    }
}
