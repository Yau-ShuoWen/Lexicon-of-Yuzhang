package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Format.StyleParams;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 南昌话拼音方案
 *
 * @author 姚说文 豫章鸿也
 */

public class NamPinyin extends UniPinyin
{

    //  ̀   ́   ̂   ̃   ̄   ̅   ̆   ̇   ̈   ̉   ̊   ̋   ̌   ̍   ̎   ̏   ̐   ̑   ̒
    // ̓   ̔   ̕   ̖   ̗   ̘   ̙   ̚   ̛   ̜   ̝   ̞   ̟   ̠   ̡   ̢
    //  ̣   ̤   ̥   ̦   ̧   ̨   ̩   ̪   ̫   ̬   ̭   ̮   ̯   ̰   ̱   ̲   ̳
    // ̴   ̵   ̶   ̷   ̸   ̹   ̺   ̻   ̼   ̽   ̾   ̿   ̀   ́   ͂   ̓   ̈́
    // ͅ   ͆   ͇   ͈   ͉   ͊   ͋   ͌   ͍   ͎   ͏   ͐   ͑   ͒   ͓   ͔
    // ͕   ͖   ͗   ͘   ͙   ͚   ͛   ͜   ͝   ͞   ͟   ͠   ͡   ͢   ͣ   ͤ
    // ͥ   ͦ   ͧ   ͨ   ͩ   ͪ   ͫ   ͬ   ͭ   ͮ   ͯ

    static char[] mark = {' ', '̀', '́', '̌', '̄', '̃', '̏', '̋'};

    public NamPinyin(String s)
    {
        super(s);//按照通用格式格式化
    }

    public NamPinyin(String s, boolean v)
    {
        super(s, v);
    }

    @Override
    protected boolean isToneValid(int n)
    {
        // 数字是否是[0,7]，如果要简单判断直接返回这句话即可
        boolean range = (n >= 0 && n < mark.length);
        // 是否配上合适的韵尾
        boolean rhythm = true;

        if (pinyin == null || pinyin.isEmpty()) return false;
        //虽然也不知道有没有用，反正放在这里

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

        final List<Pair<String, String>> rule = List.of(
                Pair.of("yi", "i"),
                Pair.of("wu", "u"),
                Pair.of("yu", "v"),
                Pair.of("w", "u"),
                Pair.of("y", "i"),
                Pair.of("ju", "jv"),
                Pair.of("qu", "qv"),
                Pair.of("xu", "xv")
        );

        for (Pair<String, String> p : rule)
        {
            if (pinyin.startsWith(p.getLeft()))
            {
                pinyin = p.getRight() + pinyin.substring(p.getLeft().length());
                break;
            }
        }

        if (pinyin.endsWith("ao"))
        {
            pinyin = pinyin.substring(0, pinyin.length() - 2) + "au";
        }
    }

    /**
     * 默认配置的转字符串
     */
    @Override
    public String toString()
    {
        //默认配置
        return toString(new NamStyle());
    }

    /**
     * 具体配置
     *
     * @param params 具體配置，這裡檢查參數，下放到具体操作函数
     */
    @Override
    public String toString(StyleParams params)
    {
        if (isInvalid()) return INVALID_PINYIN;
        show = pinyin;

        NamStyle p = (params instanceof NamStyle) ? (NamStyle) params : new NamStyle();

        addMark(p.num);//加音调
        setFormat(p.yu, p.gn, p.ee, p.oe, p.ii, p.ptk, p.alt, p.capital);
        return " //" + show + "// ";
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
        show = s;
    }


    /**
     * 将一个音节字符串转码为一个五位数字字符串，便于音素结构的分析与处理。
     *
     * <p>返回值为五位数字，表示该拼音音节的结构组成，包括声母、介音、韵尾和主元音。
     *
     * <p>编码格式如下：
     * <ol>
     *   <li>前两位（第1-2位）：声母代码，从 01 开始编码，顺序如下：
     *       <ul>
     *         00 - 零声母（无声母） 01 - b，02 - p，03 - m，04 - f，05 - d，06 - t，07 - l，08 - g，09 - k
     *         10 - ng，11 - h，12 - j，13 - q，14 - n，15 - x，16 - z，17 - c，18 - s
     *       </ul>
     *   </li>
     *   <li>第三位：韵尾代码（W）：
     *       <ul>
     *         <li>0 - 无韵尾，1 - 用于ㄗㄘㄙ类，3 - i 尾，4 - u 尾，5 - n 尾</li>
     *         <li>6 - ng 尾，7 - t 尾，8 - k 或 ʔ 尾，9 - l 尾</li>
     *       </ul>
     *   </li>
     *   <li>第四位：介音（韵头）代码（J）：
     *       <ul>
     *         <li>0 - 无介音，1 - i，2 - u，3 - ü/v</li>
     *       </ul>
     *   </li>
     *   <li>第五位：中心元音（Y）代码：
     *       <ul>
     *         <li>0 - 无主元音（默认），1 - a，2 - o，3 - e，4 - ee，5 - oe，7 - u</li>
     *       </ul>
     *   </li>
     * </ol>
     *
     * @return 编码后的五位字符串表示音节结构，例如 qiung->"13617"
     */
    @Override
    public void toCode()
    {
        String s = pinyin;
        StringBuilder Str = new StringBuilder(s);

        int S = 0, J = 0, Y = 0, W = 0;
        int l = 0, r = s.length();
        String Sub;

        //声母

        switch (Str.charAt(0))
        {
            case 'b':
                S += 1; break;
            case 'p':
                S += 2; break;
            case 'm':
                S += 3; break;
            case 'f':
                S += 4; break;
            case 'd':
                S += 5; break;
            case 't':
                S += 6; break;
            case 'l':
                S += 7; break;
            case 'g':
                S += 8; break;
            case 'k':
                S += 9; break;
            case 'h':
                S += 11; break;
            case 'j':
                S += 12; break;
            case 'q':
                S += 13; break;
            case 'n':
                if (Str.length() > 1 && Str.charAt(1) == 'g')
                {
                    S += 10; l++;
                }
                else
                {
                    S += 14;
                }
                break;
            case 'x':
                S += 15; break;
            case 'z':
                S += 16; break;
            case 'c':
                S += 17; break;
            case 's':
                S += 18; break;
            default:
                l--;
                break;
        }
        l++;

        Sub = Str.substring(l, r);
        l = 0; r = Sub.length();

        String answer = (S < 10) ? ("0" + S) : ("" + S);

        if (Sub.equals("ii"))
        {
            code = answer + "100";
            return;
        }
        if (Sub.isEmpty())
        {
            code = answer + "000";
            return;
        }


        if (l < Str.length())
        {
            switch (Sub.charAt(l))
            {
                case 'i':
                    J += 1;
                    break;
                case 'u':
                    J += 2;
                    break;
                case 'v':
                    J += 3;
                    break;
                default:
                    l--;
                    break;
            }
            l++;
        }
        Sub = Sub.substring(l, r);
        l = 0; r = Sub.length();

        if (!Sub.isEmpty())
        {
            switch (Sub.charAt(Sub.length() - 1))
            {
                case 'i':
                    W += 3;
                    break;
                case 'u':
                    W += 4;
                    break;
                case 'n':
                    W += 5;
                    break;
                case 'g':
                    W += 6; r--;
                    break;
                case 't':
                    W += 7;
                    break;
                case 'k':
                    W += 8;
                    break;
                case 'l':
                    W += 9;
                    break;
                default:
                    r++;
                    break;
            }
            r--;
            Sub = Sub.substring(l, r);
        }

        if (!Sub.isEmpty())
        {
            switch (Sub)
            {
                case "a":
                    Y += 1; break;
                case "o":
                    Y += 2; break;
                case "e":
                    Y += 3; break;
                case "ee":
                    Y += 4; break;
                case "oe":
                    Y += 5; break;
                case "u":
                    Y += 7; break;
            }
        }
        code = answer + W + J + Y;
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
     * @param num <ul>
     *                                                                                  <li>0 - 不加音调</li>
     *                                                                                  <li>1 - 智能添加，符合规范</li>
     *                                                                                  <li>2 - 符号音调加到后面</li>
     *                                                                                  <li>3 - 数字音调加到后面</li>
     *                                                                              </ul>
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
                    //TODO: 待办：是否需要iu并排标载后？ 回复：暂时不管
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
                show = pinyin + mark[tone];
                break;
            case 3:
                show = pinyin + tone;
                break;
        }
    }


//    /**
//     * 一列字符串读取为一个数组的拼音
//     */
//    public static List<NamPinyin> toPinyinList(String s)
//    {
//        String[] arr = s.split(" ");
//        List<NamPinyin> list = new ArrayList<>();
//        for (String str : arr)
//        {
//            NamPinyin np = new NamPinyin(str);
//            if (np.pinyin != null) list.add(np);
//        }
//        return list;
//    }
//
//    /**
//     * 一个数组的拼音转一个数组的字符串
//     */
//    public static List<String> toList(List<NamPinyin> list)
//    {
//        List<String> ans = new ArrayList<>();
//        for (NamPinyin np : list)
//        {
//            ans.add(np.toString());
//        }
//        return ans;
//    }
//
//
//    public static List<String> toList(List<NamPinyin> list,
//                                      StyleParams params)
//    {
//        List<String> ans = new ArrayList<>();
//        for (NamPinyin np : list)
//        {
//            ans.add(np.toString(params));
//        }
//        return ans;
//    }

    public static String parseAndReplace(String str)
    {
        return parseAndReplace(str, null);
    }

    // 静态方法，处理输入字符串
    public static String parseAndReplace(String str, NamStyle style)
    {
        StringBuilder result = new StringBuilder();
        int start = 0;

        while (true)
        {
            int open = str.indexOf('[', start);
            if (open == -1)
            {
                result.append(str.substring(start));
                break;
            }
            int close = str.indexOf(']', open);
            if (close == -1)
            {
                result.append(str.substring(start));
                break;
            }

            result.append(str, start, open);

            String content = str.substring(open + 1, close);
            NamPinyin np = new NamPinyin(content);
            result.append((style == null) ? np.toString() : np.toString(style));

            start = close + 1;
        }

        return result.toString();
    }
}
