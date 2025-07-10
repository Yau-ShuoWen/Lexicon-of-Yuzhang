package com.shuowen.yuzong.Linguistics.Format;

import lombok.Data;

@Data
public class JyutStyle extends StyleParams
{
    /**
     * 統一管理參數
     *
     * @param plan    现有拼音方案 <ul>
     *                <li>0 - 通用粤语拼音（数据库处理格式）</li>
     *                <li>1 - 香港语言学学会方案（音调1~6）</li>
     *                <li>2 - 香港语言学学会方案（音调1~9）</li>
     *                <li>3 - 耶鲁大学拼音方案（附标版本）</li>
     *                <li>4 - 耶鲁大学拼音方案（全字母版本）</li>
     *                <li>5 - 广州方案方案</li>
     *                <li>6 - 香港教育院方案（音调1~6）</li>
     *                <li>7 - 香港教育院方案（音调1~9）</li>
     *
     *                </ul>
     * @param capital 大写格式控制： <ul>
     *                <li>0 - 全部小写</li>
     *                <li>1 - 全部大写</li>
     *                <li>2 - 首字母大写</li>
     *                </ul>
     * @param num     使用什么表示音调 <ul>
     *                <li>0 - 不加音调</li>
     *                <li>1 - 加入音调</li>
     *                </ul>
     */
    public JyutStyle(int plan, int capital, int num)
    {
        this.num = num;
        this.plan = plan;
        this.capital = capital;
    }

    public int plan = 1;

    public JyutStyle()
    {
        plan=1;
        num=1;
        capital=0;
    }

    public JyutStyle(int[] a)
    {

    }

    /**
     * 返回方案的数量，方便遍历
     * */
    public int getCnt()
    {
        return 8;
    }

    public String getPlanName()
    {
        String[] s={"数据库方案","香港语言学学会方案（音调1~6）","香港语言学学会方案（音调1~9）","耶鲁大学拼音方案（附标版本）",
                "耶鲁大学拼音方案（全字母版本）","广州方案","香港教育院方案（音调1~6）","香港教育院方案（音调1~9）"};
        return s[plan];
    }
}
