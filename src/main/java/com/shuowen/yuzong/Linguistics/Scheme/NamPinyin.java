package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;

import java.util.Objects;

/**
 * 南昌话拼音方案
 */
public class NamPinyin extends UniPinyin<NamStyle>
{
    protected NamPinyin(String s)
    {
        super(s);
    }

    public static Maybe<NamPinyin> tryOf(String s)
    {
        try
        {
            return Maybe.exist(new NamPinyin(s));
        } catch (InvalidPinyinException e)
        {
            return Maybe.nothing();
        }
    }

    protected String initCode()
    {
        try
        {
            String ans = "";
            String py = pinyin;
            int idx;


            // 声母：特殊的地方只有
            // 1. n和ng都是n开头，需要具体区分
            // 2. 除了零声母识别，ng识别长度为2，其他都是1位（所以统一idx=1，其他的调整）
            idx = 1;
            ans += switch (StringTool.substring(py, 0, 1))
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


            // 对特殊的韵母处理，m n ng 直接赋值返回
            // 流程：如果被截取声母之后拼音没有了，如果是m n ng，识别为成音节辅音，否则返回失败
            if (py.isEmpty())
            {
                return switch (ans)
                {
                    case "03" -> "00007";
                    case "14" -> "00008";
                    case "10" -> "00009";
                    default -> throw new IllegalArgumentException("声母之后没有内容了");
                };
            }
            // 检查特殊韵母zii cii sii
            if (ObjectTool.existEqual(ans, "16", "17", "18") && py.startsWith("i"))
            {
                if (py.equals("ii")) return ans + "100";
                else throw new IllegalArgumentException("zcs后面接的不是ii");
            }


            // 介母：左指针统一移动一位
            idx = 1;
            ans += switch (StringTool.substring(py, 0, 1)) // 删掉了开头的就是现在的
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
            ans += switch (StringTool.substring(py, py.length() - 1))
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


            ans += switch (py)
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

            ans = StringTool.swap(ans, 2, 3);  // 识别和显示优先级不同

            if (ans.length() != 5) throw new IndexOutOfBoundsException();// 是否有效位数
            return ans;
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) // 这里面拼音出现了任何错误，就认为是无效的，所以里面可以大胆sub和charAt
        {
            throw new InvalidPinyinException("拼音编码出现异常");
        }
    }

    protected void checkEncodable()
    {
        String c = code;
        if (c.length() < 5) throw new InvalidPinyinException("code不是5位");
        String reverse = switch (c.substring(0, 2))
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

        if (!Objects.equals(pinyin, reverse))
            throw new InvalidPinyinException("没有正确逆推");
    }

    protected void checkToneValid()
    {
        if (!NumberTool.closeBetween(tone, 0, 7)) throw new InvalidPinyinException("音调范围超出");

        boolean end = ObjectTool.existEqual(StringTool.back(pinyin), 't', 'k');
        if (NumberTool.closeBetween(tone, 1, 5)) if (end) throw new InvalidPinyinException("非入声音调配对入声韵尾");
        if (NumberTool.closeBetween(tone, 6, 7)) if (!end) throw new InvalidPinyinException("入声音调配对非入声韵尾");
    }

    protected char initMark()
    {
        char[] mark = {' ', '̀', '́', '̌', '̄', '̉', '̋', '̏'};
        return mark[tone];
    }

    protected int initCorner()
    {
        int[] fourCorner = {0, 1, 2, 3, 5, 6, 7, 8}; // 没有写错，没有4是因为南昌话没有「阳上」音
        return fourCorner[tone];
    }

    protected String initWeight()
    {
        return code + tone;
    }

    public int getInitialLen()
    {
        return 2;
    }

    @Override
    public String toString()
    {
        return "默认的南昌话拼音：" + pinyin + tone + "（未知格式）";
    }

    @Override
    public String toString(NamStyle p)
    {
        NullTool.checkNotNull(p);

        String builder = setFormat(p.getYu(), p.getGn(), p.getEe(), p.getOe(), p.getIi(), p.getPtk(), p.getAlt(), p.getCapital());
        builder = addMark(builder, p.getNum());

        return " [" + builder + "] ";
    }

    public String setFormat(int yu, int gn, int ee, int oe, int ii, int ptk, int alt, int capital)
    {
        String s = pinyin;
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
            char c = StringTool.back(s);
            if (c == 't' || c == 'k')
            {
                s = StringTool.deleteBack(s);
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
                    if (ObjectTool.existEqual(s, "i", "it", "iu", "in"))
                        s = "y" + s;
                    else s = "y" + s.substring(1);
                }
                if (c == 'u')
                {
                    if (s.length() >= 2 && ObjectTool.existEqual(s.charAt(1), 'a', 'o'))
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
        return s;
    }

    protected String addMark(String builder, int num)
    {
        return switch (num)
        {
            case 1 ->
            {
                if (tone == 0) yield builder; //不用加任何符号

                StringBuilder str = new StringBuilder(builder);

                if ("ng".equalsIgnoreCase(builder))
                {
                    str.insert(1, mark);
                    yield str.toString();
                }

                int idx = -1;
                int i = str.length();

                /*
                 * 问：为什么不用包含大小写的正则表达式？
                 * 答：因为经过测试无法正确识别(ẹ Ẹ)(ё Ё)(ọ Ọ)，所以用回直接匹配大小写 25/11/10
                 * */
                while (i-- > 0)
                {
                    char c = str.charAt(i);
                    if (String.valueOf(c).matches("[aAoOöÖọỌeEẹẸёЁ]"))
                    {
                        idx = i;
                        break;
                    }
                    if (String.valueOf(c).matches("[iIịỊuUvVüÜụỤ]")) idx = i;
                }

                if (idx == -1) str.append(mark);
                else str.insert(idx + 1, mark);

                yield str.toString();
            }
            case 2 -> builder + tone;
            case 3 -> builder + " " + mark;
            default -> builder; // 0 也就是不加的意思，和默认不加是重合的
        };
    }
}
