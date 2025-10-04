package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;

import java.util.*;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 南昌话拼音方案
 *
 * @author 说文 豫章鸿也
 */

public class NamPinyin extends UniPinyin<NamStyle>
{
    static char[] mark = {' ', '̀', '́', '̌', '̄', '̉', '̋', '̏'};

    public NamPinyin(String s)
    {
        super(s);//按照通用格式格式化
    }

    public NamPinyin(String s, boolean v)
    {
        super(s, v);
    }

    /**
     * 基本过程
     * <ul>
     *     <li>条件1：范围在0-7</li>
     *     <li>条件2:1-5不是入声尾，6-7是（0，可以都是）</li>
     * </ul>
     */
    @Override
    protected boolean toneValid()
    {
        int n = tone;

        // 数字是否是[0,7]，如果要简单判断直接返回这句话即可
        boolean range = (n >= 0 && n < mark.length);
        // 是否配上合适的韵尾
        boolean rhythm = true;

        char last = pinyin.charAt(pinyin.length() - 1);
        if (n >= 1 && n <= 5)
        {
            // 不是入声，但是结尾是t或k
            if (last == 't' || last == 'k') rhythm = false;
        }
        if (n >= 6 && n <= 7)
        {
            // 为入声，但是韵尾既不为t，也不为k
            if (last != 't' && last != 'k') rhythm = false;
        }
        return range && rhythm;
    }

    @Override
    protected void scan()
    {
        pinyin = pinyin.toLowerCase();

        /* 过滤原则
         * 1.ü可以写作yu，数据库层是v
         * 2.ẹ可以写作ee，数据库层也是ee
         * */
        final List<Pair<String, String>> ruleReplace = List.of(
                Pair.of("yu", "v"),
                Pair.of("ü", "v"),
                Pair.of("ẹ", "ee")
        );


        /* 过滤原则：
         * 1.零声母的y或w：规范化为i或u
         * 2.jqx开头的u：规范化为v
         * */
        final List<Pair<String, String>> ruleBegin = List.of(
                Pair.of("yi", "i"),
                Pair.of("wu", "u"),
                Pair.of("w", "u"),
                Pair.of("y", "i"),
                Pair.of("ju", "jv"),
                Pair.of("qu", "qv"),
                Pair.of("xu", "xv"),
                Pair.of("fi", "feei")
        );

        /* 过滤原则：
         * 1.韵母为ao：规范化为au
         * 2.iv后的an：规范化为en
         * 3.普通话常用读音修改：wei->uei（上一部）->ui you->iou->iu wen->uen->un
         * 4.zcs后面没有加足两个i：补全
         * */
        final List<Pair<String, String>> ruleEnd = List.of(
                Pair.of("ao", "au"),
                Pair.of("ian", "ien"),
                Pair.of("van", "ven"),
                Pair.of("uen", "un"),
                Pair.of("uei", "ui"),
                Pair.of("iou", "ieu"),
                Pair.of("zi", "zii"),
                Pair.of("ci", "cii"),
                Pair.of("si", "sii"),
                Pair.of("z", "zii"),
                Pair.of("c", "cii"),
                Pair.of("s", "sii")
        );

        for (var p : ruleReplace)
        {
            pinyin = pinyin.replace(p.getLeft(), p.getRight());
        }

        for (var p : ruleBegin)
        {
            if (pinyin.startsWith(p.getLeft()))
            {
                pinyin = p.getRight() + pinyin.substring(p.getLeft().length());
                break;
            }
        }
        for (Pair<String, String> p : ruleEnd)
        {
            if (pinyin.endsWith(p.getLeft()))
            {
                pinyin = pinyin.substring(0, pinyin.length() - p.getLeft().length()) + p.getRight();
                break;
            }
        }
    }

    /**
     * 默认配置的转字符串
     */
    @Override
    public String toString()
    {
        //默认配置
        return toString(defaultStyle());
    }

    /**
     * 具体配置
     *
     * @param p 具體配置，這裡檢查參數，下放到具体操作函数，instanceof 是空安全的，所以不用担心null
     */
    @Override
    public String toString(NamStyle p)
    {
        if (!valid) return INVALID_PINYIN;
        show = pinyin;

        p = (p == null) ? defaultStyle() : p;

        addMark(p.getNum());//加音调
        setFormat(p.getYu(), p.getGn(), p.getEe(), p.getOe(), p.getIi(), p.getPtk(), p.getAlt(), p.getCapital());
        return " //" + show + "// ";
    }

    @Override
    protected NamStyle defaultStyle()
    {
        return new NamStyle();
    }

    /**
     * 将输入的拼音字符串根据指定的参数选项进行风格转换，用于处理方言拼音的展示或输出格式。
     *
     * @param yu      ü 的处理方式：  <ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 将 v 替换为 ü</li>
     *                <li>2 - 将 v 替换为 yu</li>
     *                </ul>
     * @param gn      "gn" 音的处理方式： <ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 将 v 替换为 ü</li>
     *                <li>2 - 将 v 替换为 yu</li>
     *                </ul>
     * @param ee      ee 的处理方式：<ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 替换为 ё</li>
     *                <li>2 - 替换为 ẹ</li>
     *                </ul>
     * @param oe      oe 的处理方式：<ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 替换为 ё</li>
     *                <li>2 - 替换为 ẹ</li>
     *                </ul>
     * @param ii      ii 的处理方式：  <ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 替换为 i</li>
     *                <li>1 - 直接用zcs</li>
     *                </ul>
     * @param ptk     入声尾音的处理（用于 t, k 结尾）：  <ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 删除结尾的 t 或 k</li>
     *                <li>2 - 将结尾的 t 或 k 替换为 h</li>
     *                <li>3 - 将结尾的 t 或 k 替换为 q</li>
     *                </ul>
     * @param alt     替代声母规则：<ul>
     *                <li>0 - 不处理</li>
     *                <li>1 - 删除结尾的 t 或 k</li>
     *                <li>2 - 将结尾的 t 或 k 替换为 h</li>
     *                <li>3 - 将结尾的 t 或 k 替换为 q</li>
     *                </ul>
     * @param capital 大写格式控制：<ul>
     *                <li>0 - 全部小写</li>
     *                <li>1 - 全部大写</li>
     *                <li>2 - 首字母大写</li>
     *                </ul>
     */
    public void setFormat(int yu, int gn, int ee, int oe, int ii,
                          int ptk, int alt, int capital)
    {
        String s = show;

        /*
         * 新增流程：因为是先标注音调再
         * */
        Character numBack = null;
        if (s.matches("\\p{L}+\\d"))
        {
            numBack = s.charAt(s.length() - 1);
            s = s.substring(0, s.length() - 1);
        }

        if (gn > 0)
        {
            s = s.replace("ni", "gni");
            s = s.replace("nv", "gnv");
        }
        if (yu > 0)
        {
            if (yu == 1) s = s.replace("v", "ü");
            if (yu == 2) s = s.replace("v", "yu");
        }
        if (ee > 0)
        {
            if (ee == 1) s = s.replace("ee", "ё");
            if (ee == 2) s = s.replace("ee", "ẹ");
        }
        if (oe > 0)
        {
            if (oe == 1) s = s.replace("oe", "ö");
            if (oe == 2) s = s.replace("oe", "ọ");
            if (oe == 3) s = s.replace("oe", "o");
        }
        if (ii > 0)
        {
            if (ii == 1) s = s.replace("ii", "i");
            if (ii == 2) s = s.replace("ii", "");
        }
        if (ptk > 0)
        {
            char c = s.charAt(s.length() - 1);
            if (c == 't' || c == 'k')
            {
                s = s.substring(0, s.length() - 1);
                if (ptk == 1) s += "";
                if (ptk == 2) s += 'h';
                if (ptk == 3) s += 'q';
            }

        }
        if (alt > 0)
        {
            char c = s.charAt(0);
            if (alt == 1)//符合普通话规律
            {
                if (c == 'i')
                {
                    // i ->yi it->yit iu->yiu in->yin
                    if (s.equals("i") || s.equals("it") || s.equals("iu") || s.equals("in"))
                        s = "y" + s;
                    else s = "y" + s.substring(1);
                }
                if (c == 'u')
                {
                    if (s.length() >= 2 && (s.charAt(1) == 'a' || s.charAt(1) == 'o'))
                        s = "w" + s.substring(1);
                    else s = "w" + s;
                }
                if (c == 'v' || c == 'ü')
                {
                    s = s.replace("v", "yu");
                    s = s.replace("ü", "yu");//转到yu处理逻辑
                }
            }
            if (alt == 2)//硬加
            {
                if (c == 'i' || c == 'v') s = "y" + s;
                if (c == 'u') s = "w" + s;
            }
        }
        if (capital > 0)
        {
            if (capital == 1) s = s.toUpperCase();
            if (capital == 2) s = s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        if (numBack != null) s += numBack;
        show = s;
    }


    /**
     * @return 五位数字，表示该拼音音节的结构组成，包括声母、介音、韵尾和主元音。编码格式如下：
     * <ol>
     * <li>前两位，声母：<p>
     * {@code 00: 零声母/无声母} | {@code 01: b} | {@code 02: p} | {@code 03: m} | {@code 04: f} | 
     * {@code 05: d} | {@code 06: t} | {@code 07: l} | {@code 08: g} | {@code 09: k} | {@code 10: ng} | {@code 11: h} | 
     * {@code 12: j} | {@code 13: q} | {@code 14: n} | {@code 15: x} | {@code 16: z} | {@code 17: c} | {@code 18: s}|
     * </li>
     *
     * <p>
     * <li>第三位，韵尾：<p>
     * {@code 0: 开元音韵尾/无韵尾} | {@code 1: z c s的整体认读} | {@code 3: i 尾} | {@code 4: u 尾} |
     * {@code 5: n 尾} | {@code 6: ng 尾} | {@code 7: t 尾} | {@code 8: k 或 ʔ 尾} | {@code 9: l 尾}|
     * </li>
     * <p>
     * <li>第四位，介音：<p>
     * {@code 0: 开口呼} | {@code 1: 合口呼：i} | {@code 2: 闭口呼：u} | {@code 3: 撮口呼：ü/v}|
     * </li>
     * <p>
     * <li>第五位：中心元音：<p>
     * {@code 0: 无主元音} | {@code 1: a} | {@code 2: o} | {@code 3: e} | {@code 4: ee} |
     * {@code 5: oe} | {@code 6: u} | {@code 7: 自成音节的m} | {@code 8: 自成音节的n} | {@code 9: 自成音节的ng}|
     * </li>
     * </ol>
     * <p>
     * 编码后的五位字符串表示音节结构，例如 {@code "qiung"->13616}
     */
    @Override
    protected void toCode()//权限非公开是因为推荐的做法是使用getCode函数
    {

        String s = pinyin;

        // 对特殊的韵母处理
        if (s.length() <= 2)
        {
            String ans = switch (s)
            {
                case "m" -> "00007";
                case "n" -> "00008";
                case "ng" -> "00009";
                default -> "";
            };
            if (!ans.isEmpty())
            {
                code = ans;
                return;
            }
        }

        StringBuilder Str = new StringBuilder(s);

        int S = 0, J = 0, Y = 0, W = 0;
        int l = 0, r = s.length();
        String Sub;

        //声母

        S = switch (Str.charAt(0))
        {
            case 'b' -> 1;
            case 'p' -> 2;
            case 'm' -> 3;
            case 'f' -> 4;
            case 'd' -> 5;
            case 't' -> 6;
            case 'l' -> 7;
            case 'g' -> 8;
            case 'k' -> 9;
            case 'h' -> 11;
            case 'j' -> 12;
            case 'q' -> 13;
            case 'n' ->
            {
                if (Str.length() > 1 && Str.charAt(1) == 'g')
                {
                    l++; yield 10;
                }
                else
                {
                    yield 14;
                }
            }
            case 'x' -> 15;
            case 'z' -> 16;
            case 'c' -> 17;
            case 's' -> 18;
            default ->
            {
                l--; yield 0;
            }
        };
        l++;

        Sub = Str.substring(l, r);
        l = 0; r = Sub.length();

        String answer = (S < 10) ? ("0" + S) : ("" + S);

        if (Sub.equals("ii"))
        {
            code = answer + "100";
            return;
        }

        if (l < Str.length())
        {
            J = switch (Sub.charAt(l))
            {
                case 'i' -> 1;
                case 'u' -> 2;
                case 'v' -> 3;
                default ->
                {
                    l--; yield 0;
                }
            };
            l++;
        }
        Sub = Sub.substring(l, r);
        l = 0; r = Sub.length();

        if (!Sub.isEmpty())
        {
            W = switch (Sub.charAt(Sub.length() - 1))
            {
                case 'i' -> 3;
                case 'u' -> 4;
                case 'n' -> 5;
                case 'g' ->
                {
                    r--; yield 6;
                }
                case 't' -> 7;
                case 'k' -> 8;
                case 'l' -> 9;
                default ->
                {
                    r++;
                    yield 0;
                }
            };
            r--;
            Sub = Sub.substring(l, r);
        }

        if (!Sub.isEmpty())
        {
            Y = switch (Sub)
            {
                case "a" -> 1;
                case "o" -> 2;
                case "e" -> 3;
                case "ee" -> 4;
                case "oe" -> 5;
                case "u" -> 6;
                default -> 0;// 2025/6/3 代号修改为6 要空出7 8 9来给m n ng
            };
        }
        code = answer + W + J + Y;
    }

    /**
     * 反向建立即可，非常简单
     * */
    protected String constuctPinyin()
    {
        String c = code;
        if (c.length() < 5) return "";
        return switch ("" + c.charAt(0) + c.charAt(1))
        {
            case "01" -> "b";
            case "02" -> "p";
            case "03" -> "m";
            case "04" -> "f";
            case "05" -> "d";
            case "06" -> "t";
            case "07" -> "l";
            case "08" -> "g";
            case "09" -> "k";
            case "10" -> "ng";
            case "11" -> "h";
            case "12" -> "j";
            case "13" -> "q";
            case "14" -> "n";
            case "15" -> "x";
            case "16" -> "z";
            case "17" -> "c";
            case "18" -> "s";
            default -> "";
        } + switch (c.charAt(3))
        {
            case '1' -> "i";
            case '2' -> "u";
            case '3' -> "v";
            default -> "";
        } + switch (c.charAt(4))
        {
            case '1' -> "a";
            case '2' -> "o";
            case '3' -> "e";
            case '4' -> "ee";
            case '5' -> "oe";
            case '6' -> "u";
            case '7' -> "m";
            case '8' -> "n";
            case '9' -> "ng";
            default -> "";
        } + switch (c.charAt(2))
        {
            case '1' -> "ii";
            case '3' -> "i";
            case '4' -> "u";
            case '5' -> "n";
            case '6' -> "ng";
            case '7' -> "t";
            case '8' -> "k";
            case '9' -> "l";
            default -> "";
        };
    }


    /**
     * 基本规则：
     * <ol>
     *     <li>如果有aoe，标注在最前面的一个aoe上</li>
     *     <li>没有，但有iuv，标在最前面的iuv上面</li>
     *     <li>没有，但是n或ng，标在最n上面</li>
     *     <li>其他（唯一情況：m），標在最後</li>
     * </ol>
     *
     * @param num 标注声调的方式  <ul>
     *            <li>0 - 不加音调</li>
     *            <li>1 - 智能添加，符合规范</li>
     *            <li>2 - 符号音调加到后面</li>
     *            <li>3 - 数字音调加到后面</li>
     *            </ul>
     */
    protected void addMark(int num)
    {
        if (tone == 0) return;//不用加任何音调

        switch (num)
        {
            case 0:
                break;
            case 1:
                StringBuilder Str = new StringBuilder(show);

                //ǹin 优先级别问题，已修改2025/4/30
                if (show.equals("ng"))
                {
                    Str.insert(1, mark[tone]);
                    return;
                }

                int idx = -1;
                int i = Str.length();
                while (i-- > 0)
                {
                    char c = Str.charAt(i);
                    if (c == 'a' || c == 'o' || c == 'e')
                    {
                        idx = i; break;
                    }
                    if (c == 'i' || c == 'u' || c == 'v')
                    {
                        idx = i;
                        //Bug:ceen2->cẹ́n sii3->sǐi 优先级别问题，已修改 2025/4/22
                        if (c == 'i' && i > 0 && Str.charAt(i - 1) == 'i') break;
                    }
                }

                if (idx == -1) Str.append(mark[tone]);
                else Str.insert(idx + 1, mark[tone]);

                show = Str.toString();
                break;
            case 2:
                show = pinyin + " " + mark[tone];
                break;
            case 3:
                show = pinyin + tone;
                break;
        }
    }

    public static NamPinyin of(String s)
    {
        return new NamPinyin(s);
    }

    public static NamPinyin of(String s, boolean v)
    {
        return new NamPinyin(s, v);
    }

    protected static Function<String, NamPinyin> creator = NamPinyin::new;

    /**
     * @see UniPinyin
     * */
    public static String formatting(String s, NamStyle style)
    {
        return UniPinyin.formatting(s, creator, style);
    }

    /**
     * @see UniPinyin
     * */
    public static List<NamPinyin> toPinyinList(String s)
    {
        return UniPinyin.toPinyinList(s, creator, "");
    }

    /**
     * @see UniPinyin
     * */
    public static List<String> toList(List<NamPinyin> list, NamStyle params)
    {
        return UniPinyin.toList(list, params);
    }

    /**
     * @see UniPinyin
     * */
    public static String splitAndReplace(String s, NamStyle style)
    {
        return UniPinyin.splitAndReplace(s, creator, style, " ");
    }

    /**
     * @see UniPinyin
     * */
    public static String parseAndReplace(String str, NamStyle style)
    {
        return UniPinyin.parseAndReplace(str, creator, style, "[", "]");
    }
}
