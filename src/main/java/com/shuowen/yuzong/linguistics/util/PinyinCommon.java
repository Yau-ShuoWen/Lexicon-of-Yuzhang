package com.shuowen.yuzong.linguistics.util;

/**
 * 拼音常用工具类，e_表示编码，d_表示解码<br>
 * 对于不常用的规则，请不要写在这个文件里，请写在各个方言的normalize和format里面。
 * <br><br>
 * 常见问题<br>
 * 0. 要不要检查？ 不要。如果有更复杂的检查，请自己实现<br><br>
 * <p>
 * 1. 要是用户把那些大写输入进来了怎么办？  应该在方言的normalize里先转小写，使丢掉这个语义。<br><br>
 * 2. 要是用户把特殊字符输入进来了怎么办？  这个不需要管。<br>
 * 写对了：<br>
 * 如果是未来存储，在normalize的时候无非跳掉这个，最后底层是一样的、<br>
 * 如果是显示，对比打字有没有打对，应该对比的是原字符串和标准的keyboard写法的区别，而不是判断是否成功解析<br>
 * 写错了：和其他写错方式没有本质区别
 */
public class PinyinCommon
{
    private PinyinCommon()
    {
    }

    public static String e_ZCS(String s)
    {
        return s.replaceFirst("^([zcs])i", "$1ı");
    }

    public static String d_ZCS(String s)
    {
        return s.replace("ı", "i");
    }

    public static String e_ZHCHSHR(String s)
    {
        return s.
                replace("zh", "ż").
                replace("ch", "ċ").
                replace("sh", "ṡ").
                replaceFirst("^([żċṡr])i", "$1ï");
    }

    public static String d_ZHCHSHR(String s)
    {
        return s
                .replace("ż", "zh")
                .replace("ċ", "ch")
                .replace("ṡ", "sh")
                .replace("ï", "i");
    }

    public static String e_Nh(String s)
    {
        if (s.startsWith("nh")) s = "ṅ" + s.substring(2);
        return s;
    }

    public static String d_Nh(String s)
    {
        if (s.startsWith("ṅ")) s = "nh" + s.substring(1);
        return s;
    }

    public static String e_Ng(String s)
    {
        return s.replace("ng", "ŋ");
    }

    public static String d_Ng(String s)
    {
        return s.replace("ŋ", "ng");
    }

    public static String e_Ao(String s)
    {
        return s.replace("ao", "au");
    }

    public static String d_Ao(String s)
    {
        return s.replace("au", "ao");
    }

    public static String e_Yi(String s)
    {
        if (s.matches("^y[^u].*")) s = s.replace("yi", "i").replace("y", "i");
        return s;
    }

    public static String e_Wu(String s)
    {
        if (s.matches("^w.*")) s = s.replace("wu", "u").replace("w", "u");
        return s;
    }

    public static String e_JQX_Ü_V_Yu_U(String s)
    {
        return s.replaceAll("([jqx])(?:ü|v|yu|u)", "$1ü");
    }

    public static String e_Ü_V_Yu(String s)
    {
        return s.replaceAll("(ü|v|yu)", "ü");
    }

    public static String e_Yu(String s)
    {
        if (s.matches("^[jqx]u.*")) s = s.replace("u", "ü");
        if (s.startsWith("yu")) s = s.replace("yu", "ü");
        if (s.equals("nv") || s.equals("nhv")) s = s.replace("v", "ü");
        return s;
    }

    public static String d_Yu_keyboard(String s)
    {
        if (s.matches("^[jqx]ü.*")) s = s.replace("ü", "u");
        if (s.startsWith("ü")) s = s.replace("ü", "yu");
        if (s.equals("nü") || s.equals("nhü")) s = s.replace("ü", "v");
        return s;
    }

    public static String d_Yu_display(String s)
    {
        if (s.matches("^[jqx]ü.*")) s = s.replace("ü", "u");
        if (s.startsWith("ü")) s = s.replace("ü", "yu");
        return s;
    }

    public static String e_A_G(String s)
    {
        return s.replace("a", "ɑ").replace("g", "ɡ");
    }

    public static String d_A_G(String s)
    {
        return s.replace("ɑ", "a").replace("ɡ", "g");
    }

    public static char toSuperScript(Integer i)
    {
        return "⁰¹²³⁴⁵⁶⁷⁸⁹".charAt(i);
    }
}

/*
 * 转义字母选择
 * 中文语境下，上加点和上加两点不会认为是新的音调，所以被NFD拆开之后会原样返回
 * - 如果是i，可以变成ï；
 * - 如果是j，没有其他变体；
 * - 如果是其他字母，可以加 ̇或者 ̈的预组合字符
 * */