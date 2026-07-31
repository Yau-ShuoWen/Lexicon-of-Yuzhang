package com.shuowen.yuzong.Linguistics.util;

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

    public static String e_ZCSR(String s)
    {
        if (s.matches("^[zcsr]i$")) s = s.charAt(0) + "ı";
        return s;
    }

    public static String d_ZCSR(String s)
    {
        return s.replace("ı", "i");
    }

    public static String e_ZHCHSH(String s)
    {
        s = s.replace("zh", "Z").replace("ch", "C").replace("sh", "S");
        if (s.matches("^[zcs]hi$")) s = s.charAt(0) + s.charAt(1) + "I";
        return s;
    }

    public static String d_ZHCHSH(String s)
    {
        return s
                .replace("Z", "zh")
                .replace("C", "ch")
                .replace("S", "sh")
                .replace("I", "i");
    }

    public static String e_Nh(String s)
    {
        if (s.startsWith("nh")) s = "N" + s.substring(2);
        return s;
    }

    public static String d_Nh(String s)
    {
        if (s.startsWith("N")) s = "nh" + s.substring(1);
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

    public static char toSuperScript(Integer i)
    {
        return "⁰¹²³⁴⁵⁶⁷⁸⁹".charAt(i);
    }
}

/*
 * 转义字母选择规则
 * 0. （前提）编译器字体显示得出来，因为要长期调试，看着难看的字体会严重影响青少年程序员的身心健康。
 * 1. 不会被NFD拆开，因为拆开错误识别就有风险，因为ToneParser里会抽走需要的NFD来自动不全音调，可能错误抽走了这里面的
 * 2. 2的例外，如果这个保证不会和拼音符号混淆，就允许：未来拼音里ToneParser规则永远不会加一个这个语义=一个声调并抽走
 *    比如ü（因为汉语使用者真的不会把  ̈ 当做声调）
 * 3. 3的补充，不可以选择上加点 ̇，因为会对i有未定义行为。
 *
 * 因为这个规则淘汰下的废稿方案
 *
 * （规则1）zh->ẑ ch->ĉ sh->ŝ 现在用ZCS
 * （规则1）nh->ñ 现在用N
 * （规则3）(zh)i->İ
 * */