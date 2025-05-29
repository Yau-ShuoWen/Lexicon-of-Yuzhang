package com.shuowen.yuzong.Linguistics.IPA;

public class IPAtransfer
{
    public static String toLine(String ipa, String tone)
    {
        /*
        * 说实话这里作者也是百思不得其解，为什么要加这一个逻辑呢
        * 据了解，这是unicode的一个规则，因为˩˩这样的两个相同的调号，常规字体Cambria等是不能渲染好的
        * 作者还特意去找了几个特殊的字体可以渲染如Doulos SIL或者Charis SIL
        * 但是使用了这些字体还是会出现一个问题：
        * - 当他后面什么字符都没有的时候，˩˩显示是正常的，宽度却仍然是一个字符的宽度
        * - 当他后面有任何字符的时候，˩˩会重新分裂成两个字符，所以作者就很崩溃
        * - 但是找到一个取巧的办法，就是换成三次重复˩˩˩，实现了宽度为两个字符
        *
        * 这个hack是在Word上测试稳定通过的
        * 参考链接 作者本人的号：https://chatgpt.com/c/68381c08-540c-8005-a331-6f7626887868
        *         外部访问：https://chatgpt.com/share/683828dd-1a64-8005-bea2-3443e3a88a35
        * 日期2025/05/29
        * */
        if(tone.length()==2&&tone.charAt(0)==tone.charAt(1))
        {
            tone=tone+tone.charAt(1);
        }


        if (tone.equals("0")) return "·" + ipa;
        else return ipa + (tone
                .replace('1', '˩')
                .replace('2', '˨')
                .replace('3', '˧')
                .replace('4', '˦')
                .replace('5', '˥'));
    }
}
