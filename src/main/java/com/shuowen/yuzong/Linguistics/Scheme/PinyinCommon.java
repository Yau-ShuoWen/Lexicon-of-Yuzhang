package com.shuowen.yuzong.Linguistics.Scheme;

/**
 * 拼音常用工具类，e_表示编码，d_表示解码
 * */
public class PinyinCommon
{
    private PinyinCommon()
    {
    }

    /**
     * zi ci si ri：i改成ı，表示虽然是i，但是底层不同<br>
     * 作者的想法：考虑到未来的扩展性，所有带上标的都是未来方言拼音可能选择的，
     * 但是中国人还能接受iI乱加点这件事吗？所以不正好土耳其语的两个符号，在未来的拼音里一定用不到。
     */
    public static String e_ZCSR(String s)
    {
        if (s.matches("^[zcsr]i$")) s = s.charAt(0) + "ı";
        return s;
    }

    /**
     * 软件内部信任为唯一可能，直接解密
     */
    public static String d_ZCSR(String s)
    {
        return s.replace("ı", "i");
    }

    public static String e_ZHCHSH(String s)
    {
        s = s.replace("zh", "ẑ").replace("ch", "ĉ").replace("sh", "ŝ");
        if (s.matches("^[zcs]hi$")) s = s.charAt(0) + s.charAt(1) + "İ";
        return s;
    }

    public static String d_ZHCHSH(String s)
    {
        return s
                .replace("ẑ", "zh")
                .replace("ĉ", "ch")
                .replace("ŝ", "sh")
                .replace("İ", "i");
    }

    public static String e_Nh(String s)
    {
        if (s.startsWith("nh")) s = "ñ" + s.substring(2);
        return s;
    }

    public static String d_Nh(String s)
    {
        if (s.startsWith("ñ")) s = "nh" + s.substring(1);
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
        return s.replaceAll("([jqx])(?:ü|v|yu|u)", "$1yu");
    }

    public static String e_Ü_V_Yu(String s)
    {
        return s.replaceAll("(ü|v|yu)", "yu");
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

    public static char toSuperScript(Integer i)
    {
        return "⁰¹²³⁴⁵⁶⁷⁸⁹".charAt(i);
    }
}
