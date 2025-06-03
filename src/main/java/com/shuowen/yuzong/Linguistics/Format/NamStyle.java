package com.shuowen.yuzong.Linguistics.Format;

import lombok.Data;

@Data
public class NamStyle extends StyleParams
{

    /**
     * 統一管理參數
     *
     * @param yu      ü 的处理方式：  <ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 将 v 替换为 ü</li>
     *                <li>2 - 将 v 替换为 yu</li>
     *                </ul>
     * @param gn      "gn" 音的处理方式： <ul>
     *                <li>0 - 保留原来的n</li>
     *                <li>1 - 恢复为gn</li>
     *                </ul>
     * @param ee      ee 的处理方式：<ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 替换为 ё</li>
     *                <li>2 - 替换为 ẹ</li>
     *                </ul>
     * @param oe      oe 的处理方式：<ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 替换为 ö</li>
     *                <li>2 - 替换为 ̣ọ</li>
     *                <li>3 - 替换为 o</li>
     *                </ul>
     * @param ii      ii 的处理方式：  <ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 替换为 i</li>
     *                <li>2 - 使用zcs</li>
     *                </ul>
     * @param ptk     入声尾音的处理（用于 t, k 结尾）：  <ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 删除结尾的 t 或 k</li>
     *                <li>2 - 将结尾的 t 或 k 替换为 h</li>
     *                <li>3 - 将结尾的 t 或 k 替换为 q</li>
     *                </ul>
     * @param alt     替代声母规则：<ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 符合普通话规律的</li>
     *                <li>2 - 直接硬在i前加y，u前加w</li>
     *                </ul>
     * @param capital 大写格式控制：<ul>
     *                <li>0 - 全部小写</li>
     *                <li>1 - 全部大写</li>
     *                <li>2 - 首字母大写</li>
     *                </ul>
     * @param num 标注声调的方式  <ul>
     *            <li>0 - 不加音调</li>
     *            <li>1 - 智能添加，符合规范</li>
     *            <li>2 - 符号音调加到后面</li>
     *            <li>3 - 数字音调加到后面</li>
     *            </ul>
     */
    public NamStyle(int yu, int gn, int ee, int oe, int ii,
                    int ptk, int alt, int capital, int num)
    {
        this.yu = yu;
        this.gn = gn;
        this.ee = ee;
        this.oe = oe;
        this.ii = ii;
        this.ptk = ptk;
        this.alt = alt;
        this.capital = capital;
        this.num = num;
    }

    public int yu = 1;
    public int gn = 0;
    public int ee = 2;
    public int oe = 3;
    public int ii = 1;
    public int ptk = 1;
    public int alt = 0;

    public NamStyle()
    {
    }

    public NamStyle(int[] a)
    {
        if (a.length > 0) yu = a[0];
        if (a.length > 1) gn = a[1];
        if (a.length > 2) ee = a[2];
        if (a.length > 3) oe = a[3];
        if (a.length > 4) ii = a[4];
        if (a.length > 5) ptk = a[5];
        if (a.length > 6) alt = a[6];
        if (a.length > 7) capital = a[7];
        if (a.length > 8) num = a[8];
    }

}
