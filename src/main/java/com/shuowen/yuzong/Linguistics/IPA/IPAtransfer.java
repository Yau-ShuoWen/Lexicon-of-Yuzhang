package com.shuowen.yuzong.Linguistics.IPA;

public class IPAtransfer
{
    public static String toLine(String str)
    {
        return str.replace('1','˩')
                .replace('2','˨')
                .replace('3','˧')
                .replace('4','˦')
                .replace('5','˥')
                .replace('0','˥');
    }
}
