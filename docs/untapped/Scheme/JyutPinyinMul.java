//package com.shuowen.yuzong.Linguistics.Scheme;
//
//import org.apache.commons.lang3.tuple.ImmutablePair;
//import org.apache.commons.lang3.tuple.Pair;
//
//import java.util.List;
//
//public class JyutPinyinMul
//{
//    /**
//     * 私有函数：是否使用GwKw
//     */
//    private static String GwKw(String s)
//    {
//        if ((s.startsWith("gu") && s.length() > 2) || (s.startsWith("ku") && s.length() > 2))
//        {
//            //gu guk gung 不变
//            if (!(s.endsWith("u") || s.endsWith("uk") || s.endsWith("ung")))
//                s = "" + s.charAt(0) + 'w' + s.substring(2);
//        }
//        return s;
//    }
//
//    /**
//     * @param ch 代声母
//     */
//    private static String Yi(String s, char ch)
//    {
//        // ji jiu jim jin jip jit jik
//        // 其他 ja je jung ... 不带i
//        if (s.charAt(0) == 'i')
//        {
//            if (s.length() == 1) s = ch + "i";
//            else
//            {
//                char c = s.charAt(1);
//                if (c == 'u' || c == 'm' || c == 'n' || c == 'p' || c == 't' || c == 'k')
//                    s = ch + s;
//                else
//                    s = ch + s.substring(1);
//            }
//        }
//        return s;
//    }
//
//    private static String Wu(String s)
//    {
//        // wu wun wut 两个都带
//        // uk ung 只有u
//        // waak wing ...只用w
//        if (s.charAt(0) == 'u')
//        {
//            if (s.length() == 1) s = "wu";
//            else// 祇有uk 和 ung 保留原型
//            {
//                if (!(s.endsWith("uk") || s.endsWith("ung")))
//                {
//                    char c = s.charAt(1);
//                    if (c == 'i' || c == 'n' || c == 't')
//                        s = "w" + s;
//                    else
//                        s = 'w' + s.substring(1);
//                }
//
//            }
//        }
//        return s;
//    }
//
//    /**
//     * 香港方案
//     */
//    public static String toGwong(String s)
//    {
//        s = Yi(s, 'j');
//        s = Wu(s);
//        s = GwKw(s);
//
//        // 开头的 ü 用 jyu 代替
//        // 剩下的 ü 都用 yu代替
//
//        if (s.contains("v"))
//        {
//            if (s.charAt(0) == 'v')//vt->jyut
//                s = s.replace("v", "jyu");
//            else //nvn->nyun
//                s = s.replace("v", "yu");
//        }
//        return s;
//    }
//
//    /**
//     * 耶鲁拼音的通用的转换，和声母韵母替换有关
//     */
//    public static String toYale(String s)
//    {
//        switch (s.charAt(0))
//        {
//            case 'z' -> s = 'j' + s.substring(1);
//            case 'c' -> s = "ch" + s.substring(1);
//            case 'i' -> s = Yi(s, 'y');
//            case 'u' -> s = Wu(s);
//            case 'g', 'k' -> s = GwKw(s);
//        }
//
//        final List<Pair<String, String>> rule = List.of(
//                Pair.of("v", "yu"),
//                Pair.of("oe", "eu"),
//                Pair.of("eo", "eu")
//        );
//        for (Pair<String, String> p : rule)
//            s = s.replace(p.getLeft(), p.getRight());
//        return s;
//    }
//
//    private static Pair<Integer, Integer> findAEIOU(String s)
//    {
//        int l = -1, r = -1;
//        String vowels = "aeiou";
//        for (int i = 0; i < s.length(); i++)
//        {
//            if (vowels.indexOf(s.charAt(i)) >= 0)
//            {
//                if (l == -1) l = i;
//                r = i;
//            }
//        }
//        l++; r++;
//
//        // 没有任何元音m ng
//        if (l == 0)
//        {
//            l = 1;
//            r = s.length();
//        }
//        return new ImmutablePair<>(l, r);
//    }
//
//    /**
//     * 耶鲁拼音的附标版本音调标注
//     */
//    public static String toYaleAffix(String show, Integer tone)
//    {
//        StringBuilder sb = new StringBuilder(show);
//        Pair<Integer, Integer> a = findAEIOU(show);
//        int l = a.getLeft(), r = a.getRight();
//
//        switch (tone)
//        {
//            case 1:
//                sb.insert(l, '̄');
//                break;
//            case 2:
//                sb.insert(l, '́');
//                break;
//            case 3:
//                break;
//            case 4:
//                sb.insert(r, 'h');
//                sb.insert(l, '̄');
//                break;
//            case 5:
//                sb.insert(r, 'h');
//                sb.insert(l, '́');
//                break;
//            case 6:
//                sb.insert(r, 'h');
//                break;
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 转换为耶鲁拼音全字母，后缀版本的音调标注
//     */
//    public static String toYaleSuffix(String show, Integer tone)
//    {
//        StringBuilder sb = new StringBuilder(show);
//        Pair<Integer, Integer> a = findAEIOU(show);
//        int r = a.getRight();
//
//        switch (tone)
//        {
//            case 1:
//                sb.insert(r, 'r');
//                break;
//            case 2:
//                sb.insert(r, 'l');
//                break;
//            case 3:
//                break;
//            case 4:
//                sb.insert(r, "rh");
//                break;
//            case 5:
//                sb.insert(r, "lh");
//                break;
//            case 6:
//                sb.insert(r, 'h');
//                break;
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 广州版本
//     */
//    public static String toGuong(String s)
//    {
//        // 尖音符合大陆的标准
//        // 跟着zcs的iü
//        char idx = s.charAt(0);
//        if (idx == 'z' || idx == 'c' || idx == 's')
//        {
//            if (s.charAt(1) == 'i' || s.charAt(1) == 'v')
//            {
//                s = switch (s.charAt(0))
//                {
//                    case 'z' -> 'j' + s.substring(1);
//                    case 'c' -> 'q' + s.substring(1);
//                    case 's' -> 'x' + s.substring(1);
//                    default -> s;
//                };
//                // 猜的
//                if (s.charAt(1) == 'v') s = s.replace("v", "u");
//            }
//        }
//
//        // 暂且和粤拼一样
//        s = Yi(s, 'y');
//        s = Wu(s);
//
//
//
//        // 入声尾不送气
//        idx = s.charAt(s.length() - 1);
//        if (idx == 'p' || idx == 't' || idx == 'k')
//        {
//            s = s.substring(0, s.length() - 1) + switch (idx)
//            {
//                case 'p' -> "b";
//                case 't' -> "d";
//                case 'k' -> "g";
//                default -> idx;
//            };
//        }
//
//        //主元音调整
//        List<Pair<String, String>> rule = List.of(
//                Pair.of("aa", "a"),
//                Pair.of("a", "e"),
//                Pair.of("oe", "ê"),
//                Pair.of("eo", "ê"),
//                Pair.of("e", "é"),
//                Pair.of("v", "ü")
//        );
//        for (Pair<String, String> p : rule)
//        {
//            if (s.contains(p.getLeft()))
//            {
//                s = s.replace(p.getLeft(), p.getRight());
//                break;
//            }
//        }
//
//        // 猜的
//        if (s.charAt(0) == 'ü') s = "yu" + s.substring(1);
//
//        if (s.endsWith("au") || s.endsWith("eu") || s.endsWith("éu"))
//            s = s.substring(0, s.length() - 1) + "o";
//
//        return s;
//    }
//
//    /**
//     * 香港教育院版本
//     */
//    public static String toMinistry(String s)
//    {
//        s = toGwong(s);
//        char idx = s.charAt(0);
//        if (idx == 'z') s = "t" + s;
//        if (idx == 'c') s = "ts" + s.substring(1);
//
//        s = s.replace("eo", "oe");
//        s = s.replace("v", "y");
//        if (s.endsWith("oei")) s = s.substring(0, s.length() - 1) + "y";
//
//        return s;
//    }
//
//    /**
//     * 转换为刘锡祥方案
//     */
//    public static String toLSC(JyutPinyin a)
//    {
//
//        /*
//         * aa->a
//         * o->oh
//         * ou->o
//         * oe/eo->eu eu->-
//         * yu
//         * */
//        return "";
//    }
//
//    /**
//     * 转换为黄锡凌方案
//     */
//    public static String toWSL(JyutPinyin a)
//    {
//        return "";
//    }
//
//
//    /**
//     * 转换为M-W方案
//     */
//    public static String toMW()
//    {
//        return "";
//    }
//
//    /***/
//    public static String toNew(JyutPinyin a)
//    {
//        return "";
//    }
//
//
//    /**
//     * 转换为粤语羅馬字
//     */
//    public static String toJyutRoma()
//    {
//        return "";
//    }
//
//    /**
//     * 孙铭泽给我转的神人方案
//     */
//    public static String toViet()
//    {
//        return "";
//    }
//
//    /**
//     * 九声六调互相转
//     * @param op true 9转6 false 6转9
//     * */
//    public static int switchTone(int t, boolean op)
//    {
//        if (op) return switch (t)
//        {
//            case 7 -> 1;
//            case 8 -> 3;
//            case 9 -> 6;
//            default -> t;
//        };
//        else return switch (t)
//        {
//            case 1 -> 7;
//            case 3 -> 8;
//            case 6 -> 9;
//            default -> t;
//        };
//    }
//}
