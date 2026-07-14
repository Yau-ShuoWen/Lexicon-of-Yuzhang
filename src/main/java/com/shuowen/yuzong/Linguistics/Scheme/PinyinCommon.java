package com.shuowen.yuzong.Linguistics.Scheme;

public class PinyinCommon
{
    private PinyinCommon()
    {

    }

    /**
     * 把{@code zi ci si}转换成把{@code zii cii sii}<br>
     * 把原来写成的{@code zii cii sii}挡掉防止hack
     */
    public static String encodeZiCiSi(String s)
    {
        if (s.matches("^[zcs]ii$")) s = "";
        if (s.matches("^[zcs]i$")) s = s.charAt(0) + "ii";
        return s;
    }

    /**
     * 把{@code zii cii sii}转换成把{@code zi ci si}
     */
    public static String decodeZiiCiiSii(String s)
    {
        if (s.matches("^[zcs]ii$")) s = s.charAt(0) + "i";
        return s;
    }

    /**
     * 新版本的编码zi ci si ri：改成ɏ<br>
     * 原因：ii好触发，ɏ不好触发，如果碰到了，说明真的巧，如果看源代码，那说明白底层
     */
    public static String e_ZCSR(String s)
    {
        if (s.matches("^[zcsr]i$")) s = s.charAt(0) + "ɏ";
        return s;
    }

    public static String d_ZCSR(String s)
    {
        if (s.matches("^[zcsr]ɏ$")) s = s.charAt(0) + "i";
        return s;
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


    public static String encodeYiFront(String s, boolean punish)
    {
        // 如果不允许，开头i的就是不合法的字符
        if (s.startsWith("i") && punish) s = "";

        // y开头，但不是yu
        if (s.matches("^y[^u].*"))
            s = s.replace("yi", "i").replace("y", "i");

        return s;
    }

    public static String encodeWuFront(String s, boolean punish)
    {
        if (s.startsWith("u") && punish) s = "";

        if (s.matches("^w.*"))
            s = s.replace("wu", "u").replace("w", "u");

        return s;
    }

    public static String encodeJuQuXu(String s, boolean punish)
    {
        if (punish && s.matches("^[jqx]yu.*")) s = "";
        if (s.matches("^[jqx]u.*")) s = s.replace("u", "yu");
        return s;
    }

    public static String decodeJyuQyuXyu(String s)
    {
        if (s.matches("^[jqx]yu.*")) s = s.replace("yu", "u");
        return s;
    }

    public static String encodeYuNotFront(String s, boolean keyboard, boolean punish)
    {
        // 在不合适的情况和位置出现了，如果要判断错误，就判断
        if (keyboard && s.contains("ü") && punish) s = "";
        if (!keyboard && s.contains("v") && punish) s = "";
        if ((s.startsWith("ü") || s.startsWith("v")) && punish) s = "";

        if (s.matches(".*[vü].*"))
            s = s.replace("v", "yu").replace("ü", "yu");

        return s;
    }

    public static String decodeYuNotFront(String s, boolean keyboard)
    {
        if (s.contains("yu"))
        {
            if (!s.startsWith("yu")) s = s.replace("yu", keyboard ? "v" : "ü");
        }
        return s;
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

    public static String encodeAuFromAo(String s, boolean punish)
    {
        if (punish && s.endsWith("au")) s = "";
        if (s.endsWith("ao")) s = s.replace("ao", "au");
        return s;
    }

    public static String decodeAuToAu(String s)
    {
        return s.replace("au", "ao");
    }

    public static char toSuperScript(Integer i)
    {
        return "⁰¹²³⁴⁵⁶⁷⁸⁹".charAt(i);
    }
}
