package com.shuowen.yuzong.linguistics.pinyinv2.lac;

/** 当前请求选择的键盘拼音方案；非请求线程始终使用正式方案。 */
public final class LacKeyboardSchemeContext
{
    private static final ThreadLocal<LACPinyin.KeyboardScheme> CURRENT = new ThreadLocal<>();

    private LacKeyboardSchemeContext() {}

    public static LACPinyin.KeyboardScheme current()
    {
        var scheme = CURRENT.get();
        return scheme == null ? LACPinyin.KeyboardScheme.STANDARD : scheme;
    }

    public static void set(LACPinyin.KeyboardScheme scheme)
    {
        CURRENT.set(scheme);
    }

    public static void clear()
    {
        CURRENT.remove();
    }
}
