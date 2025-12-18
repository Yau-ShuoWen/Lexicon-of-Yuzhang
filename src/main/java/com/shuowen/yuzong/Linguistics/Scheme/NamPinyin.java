package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;

/**
 * 南昌话拼音方案
 *
 * @author 说文 豫章鸿也
 */

public class NamPinyin extends UniPinyin<NamStyle>
{
    private static char[] mark = {' ', '̀', '́', '̌', '̄', '̉', '̋', '̏'};

    private static char[] fourCorne = {' ', '꜀', '꜁', '꜂', '꜄', '꜅', '꜆', '꜇'};

    public char getFourCornerTone()
    {
        return fourCorne[tone];
    }

    public NamPinyin(String s)
    {
        super(s);//按照通用格式格式化
    }

    public NamPinyin(String s, boolean v)
    {
        super(s, v);
    }

    public static NamPinyin of(String s)
    {
        return new NamPinyin(s);
    }

    public static NamPinyin of(String s, boolean v)
    {
        return new NamPinyin(s, v);
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

    /**
     * 默认配置的转字符串
     */
    @Override
    public String toString()
    {
        return toString(NamStyle.getStandardStyle()) + " | " + toString(NamStyle.getKeyboardStyle());
    }

    @Override
    public String toString(NamStyle p)
    {
        if (!valid) return INVALID;
        show = pinyin;

        p = NullTool.getDefault(p, NamStyle.getKeyboardStyle());

        setFormat(p.getYu(), p.getGn(), p.getEe(), p.getOe(), p.getIi(), p.getPtk(), p.getAlt(), p.getCapital());
        addMark(p.getNum());//加音调
        return " [" + show + "] ";
    }

    public void setFormat(int yu, int gn, int ee, int oe, int ii,
                          int ptk, int alt, int capital)
    {
        String s = show;
        if (gn > 0)
        {
            s = s.replace("ni", "gni");
            s = s.replace("nyu", "gnyu");
        }
        if (yu > 0)
        {
            if (yu == 1) s = s.replace("yu", "ü");
            if (yu == 2) s = s.replace("yu", "v");
            if (yu == 3) s = s.replace("yu", "ụ");
        }
        if (ee > 0)
        {
            if (ee == 1) s = s.replace("ee", "ẹ");
            if (ee == 2) s = s.replace("ee", "ё");
        }
        if (oe > 0)
        {
            if (oe == 1) s = s.replace("oe", "ọ");
            if (oe == 2) s = s.replace("oe", "ö");
            if (oe == 3) s = s.replace("oe", "o");
        }
        if (ii > 0)
        {
            if (ii == 1) s = s.replace("ii", "i");
            if (ii == 2) s = s.replace("ii", "");
            if (ii == 3) s = s.replace("ii", "ị");
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
                if (ptk == 4)
                {
                    if (c == 'k') s += 'h';
                    else s += 't';
                }
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
            }
            if (alt == 2)//硬加
            {
                if (c == 'i') s = "y" + s;
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


    @Override
    public Integer syllableLen()
    {
        return 2;
    }

    @Override
    public boolean toCode()
    {
        try
        {
            // code 置空
            code = "";
            String py = pinyin;
            int idx;

            // 对特殊的韵母处理，m n ng 直接赋值返回
            // 必须放在识别声母之前，否则就会被识别掉了
            switch (py)
            {
                case "m" -> code = "00007";
                case "n" -> code = "00008";
                case "ng" -> code = "00009";
            }
            if (!code.isEmpty()) return true;


            // 声母：特殊的地方只有
            // 1. n和ng都是n开头，需要具体区分
            // 2. 除了零声母识别，ng识别长度为2，其他都是1位（所以统一idx=1，其他的调整）
            idx = 1;
            code += switch (StringTool.substring(py, 0, 1))
            {
                case "b" -> "01";
                case "p" -> "02";
                case "m" -> "03";
                case "f" -> "04";
                case "d" -> "05";
                case "t" -> "06";
                case "l" -> "07";
                case "g" -> "08";
                case "k" -> "09";
                case "h" -> "11";
                case "j" -> "12";
                case "q" -> "13";
                case "n" ->
                {
                    // 区分是n还是ng，就是安全检测下一位是不是g
                    if (StringTool.charEquals(pinyin, 1, 'g'))
                    {
                        idx = 2;
                        yield "10";
                    }
                    else yield "14";
                }
                case "x" -> "15";
                case "z" -> "16";
                case "c" -> "17";
                case "s" -> "18";
                default ->
                {
                    idx = 0;
                    yield "00";
                }
            };
            py = py.substring(idx);

            // 检查特殊韵母
            if (py.equals("ii"))
            {
                code += "100";
                return true;
            }

            // 介母：左指针统一移动一位
            // TODO 没有完全确定ü的具体显示方式，所以这里还是待定
            idx = 1;
            code += switch (StringTool.substring(py, 0, 1)) // 删掉了开头的就是现在的
            {
                case "i" -> "1";
                case "u" -> "2";
                case "y" ->
                {
                    if (StringTool.charEquals(py, 1, 'u'))
                    {
                        idx = 2;
                        yield "3";
                    }
                    else throw new IllegalArgumentException("出现y不出现u");
                }
                default ->
                {
                    idx = 0;
                    yield "0";
                }
            };
            py = py.substring(idx);


            // 韵尾：特殊的地方只有
            // 除了没有韵尾不移动，ng要移动两位，其他都是移动一位（所以统一移动一位，其他的调整）
            idx = 1;
            code += switch (StringTool.substring(py, py.length() - 1))
            {
                case "i" -> "3";
                case "u" -> "4";
                case "n" -> "5";
                case "g" ->    // 这里有g没有n怎么办？encodable会检查能不能反过来的
                {
                    idx = 2;
                    yield 6;
                }
                case "t" -> "7";
                case "k" -> "8";
                case "l" -> "9";
                default ->
                {
                    idx = 0;
                    yield "0";
                }
            };
            py = py.substring(0, py.length() - idx);


            code += switch (py)
            {
                case "a" -> "1";
                case "o" -> "2";
                case "e" -> "3";
                case "ee" -> "4";
                case "oe" -> "5";
                case "u" -> "6";
                default ->
                {
                    // 没有在已有的情况下识别到主元音
                    // py为空，如iu i为介母 u为韵尾，正常置空
                    // 不然说明剩下的格式不正确
                    if (!py.isEmpty()) throw new IndexOutOfBoundsException();
                    else yield "0";
                }
            };

            code = StringTool.swap(code, 2, 3);  // 识别和显示优先级不同
            if (code.length() == 5) return true;      // 是否有效位数
            else
            {
                code = INVALID;
                return false;
            }
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) // 这里面拼音出现了任何错误，就认为是无效的，所以里面可以大胆sub和charAt
        {
            code = INVALID;
            return false;
        }
    }


    /**
     * 反向建立即可，非常简单
     */
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
            case '3' -> "yu";
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


    protected void addMark(int num)
    {
        if (tone == 0) return;//不用加任何音调

        switch (num)
        {
            case 0:
                break;
            case 1:
                StringBuilder Str = new StringBuilder(show);

                if ("ng".equalsIgnoreCase(show))
                {
                    Str.insert(1, mark[tone]);
                    show = Str.toString();
                    return;
                }

                int idx = -1;
                int i = Str.length();

                /*
                 * 问：为什么不用包含大小写的正则表达式？
                 * 答：因为经过测试无法正确识别(ẹ Ẹ)(ё Ё)(ọ Ọ)，所以用回直接匹配大小写 25/11/10
                 * */
                while (i-- > 0)
                {
                    char c = Str.charAt(i);
                    if (String.valueOf(c).matches("[aAoOöÖọỌeEẹẸёЁ]"))
                    {
                        idx = i;
                        break;
                    }
                    if (String.valueOf(c).matches("[iIịỊuUvVüÜụỤ]")) idx = i;
                }

                if (idx == -1) Str.append(mark[tone]);
                else Str.insert(idx + 1, mark[tone]);

                show = Str.toString();
                break;
            case 2:
                show = show + tone;
                break;
            case 3:
                show = show + " " + mark[tone];
                break;
        }
    }
}
