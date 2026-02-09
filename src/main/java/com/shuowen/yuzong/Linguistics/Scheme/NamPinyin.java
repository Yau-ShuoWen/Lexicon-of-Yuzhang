package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.NumberTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ObjectTool;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinDetail;

import java.util.List;
import java.util.Objects;

import static com.shuowen.yuzong.data.domain.Pinyin.PinyinFormatter.trySplit;

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

    @Override
    public String toString()
    {
        return "默认的南昌话拼音：" + pinyin + tone + "（未知格式）";
    }

    @Override
    public String toString(NamStyle p)
    {
        NullTool.checkNotNull(p);

        String builder = setFormat(p.getYu(), p.getGn(), p.getEe(), p.getOe(), p.getIi(), p.getPtk(), p.getYw());
        builder = addMark(builder, p.getNum(), p.getIu());
        builder = setCapital(builder, p.getCapital());

        return " [" + builder + "] ";
    }

    public String setFormat(int yu, int gn, int ee, int oe, int ii, int ptk, int yw)
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
        if (yw > 0)
        {
            char c = s.charAt(0);
            if (yw == 1)//符合普通话规律
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
            if (yw == 2)//硬加
            {
                if (c == 'i') s = "y" + s;
                if (c == 'u') s = "w" + s;
            }
        }
        return s;
    }

    protected String addMark(String builder, int num, int iu)
    {
        return switch (num)
        {
            case 1 ->
            {
                if (tone == 0) yield builder; //不用加任何符号

                // 标在后的情况下iu是例外，都是本来应该标在i上，根据这个规则标在u上反之亦然
                if (iu == 1 && builder.contains("iu")) yield builder.replace("u", "u" + mark);
                if (iu == 2 && builder.contains("ui")) yield builder.replace("u", "u" + mark);
                // 通常情况按照顺序识别
                for (String i : "aoöọeẹёiịuvüụ".split(""))
                    if (builder.contains(i)) yield builder.replace(i, i + mark);

                // 例外：没有主元音m n ng，只有ng要特殊处理
                if ("ng".equals(builder)) yield StringTool.insert(builder, 1, mark);
                else yield builder + mark;
            }
            case 2 -> builder + tone;
            case 3 -> builder + " " + mark;
            default -> builder; // 0 也就是不加的意思，和异常参数的静默处理是重合的
        };
    }

    protected String setCapital(String builder, int capital)
    {
        if (capital > 0)
        {
            if (capital == 1) return builder.toUpperCase();
            if (capital == 2) return builder.substring(0, 1).toUpperCase() + builder.substring(1);
        }
        return builder;
    }


    public static String normalize(String text)
    {
        // 《智能是一个巨大的if-else语句》
        text = text.toLowerCase();

        var tmp = trySplit(text);
        text = tmp.getLeft();
        int tone = tmp.getRight();

        // 处理 i 在开头：
        // 匹配：y开头，但不是yu
        // 写法处理：yi->i  yit->it  ya->ia
        if (text.matches("^y[^u].*")) text = text.replace("yi", "i").replace("y", "i");

        // 处理 u 在开头：
        // 匹配：w开头
        // 写法处理：wu->u  wut->ut  wa->ua
        if (text.matches("^w.*")) text = text.replace("wu", "u").replace("w", "u");

        // 处理 ü 在字符串中：
        // 匹配：字符串包含v或者ü
        // 写法处理：lve-> lyue  ün->yun
        if (text.matches(".*[vü].*")) text = text.replace("v", "yu").replace("ü", "yu");

        // 处理 ju qu xu：
        // 匹配，第一个字符是jqx，第二个字母是u
        // 写法处理：ju->jyu   que->qyue   xuen->xyuen
        if (text.matches("^([jqx])u.*")) text = text.replace("u", "yu");

        // 单独处理 feei
        if (text.equals("fi")) text = text.replace("fi", "feei");

        // 处理 ẹ
        if (text.contains("ẹ")) text = text.replace("ẹ", "ee");
        if (text.contains("ọ")) text = text.replace("ọ", "oe");

        // 处理 zii cii sii 后面i不足量的问题：
        // 匹配：第一个字母是zcs，没有或有一个i，然后立刻结束字符串
        // 写法处理：z->zii   ci->cii   s->sii
        if (text.matches("^[zcs]i?$")) text = text.charAt(0) + "ii";

        // 双韵母的模糊处理
        // 匹配：普通话常见但是不符合的： ao iao ou iou uei
        // 特殊：iou/uei的简写歪打正着iu/ui，这里的iou/uei实际上是从you/wei变过来的
        // ao->au  iau->ieu  ou->eu  iou->iu
        if (text.contains("ao")) text = text.replace("ao", "au");
        if (text.contains("iau")) text = text.replace("iau", "ieu");
        if (text.contains("ou"))
        {
            if (text.contains("iou"))
                text = text.replace("iou", "iu");
            else text = text.replace("ou", "eu");
        }
        if (text.contains("uei")) text = text.replace("uei", "ui");

        // 鼻韵母的模糊处理
        // 匹配：普通话常见但是不符合的： ian yuan uen
        // 特殊：uen的简写歪打正着un，这里的uen实际上是从wen变过来的
        // ian->ien  yuan->yuon  uen->un
        if (text.contains("ian")) text = text.replace("ian", "ien");
        if (text.contains("yuan")) text = text.replace("yuan", "yuon");
        if (text.contains("uen")) text = text.replace("uen", "un");

        return text + tone;
    }

    public static List<List<PinyinDetail>> tablizer()
    {
        var tone = PinyinDetail.listOf(
                "tone",
                "a|à|á|ǎ|ā|ả|a̋|ȁ",
                "a0|a1|a2|a3|a4|a5|a6|a7"
        );
        var initial = PinyinDetail.listOf(
                "initial",
                "b|p|m|f|d|t|l|g|k|ng|h|j|q|n|x|z|c|s",
                "b|p|m|f|d|t|l|g|k|ng|h|j|q|n|x|z|c|s"
        );
        var lastWithSingle = PinyinDetail.listOf( // ii 去除，简化
                "lastWithSingle",
                "a|o|e|ọ|ẹ|i|u|ü",
                "a|o|e|oe|ee|i|u|yu"
        );
        var lastWithDouble = PinyinDetail.listOf(
                "lastWithDouble",
                "ai|uai|ẹi|ui|au|eu|ieu|ẹu|iu",
                "ai|uai|eei|ui|au|eu|ieu|eeu|iu"
        );
        var lastWithNasal = PinyinDetail.listOf(
                "lastWithNasal",
                "an|on|en|ẹn|in|un|ün|ang|ong|ung|iung",
                "an|on|en|een|in|un|yun|ang|ong|ung|iung"
        );
        var lastWithShort = PinyinDetail.listOf(
                "lastWithShort",
                "at|ot|et|ẹt|it|ut|üt|ak|ok|uk|iuk",
                "at|ot|et|eet|it|ut|yut|ak|ok|uk|iuk"
        );
        return List.of(tone, initial, lastWithSingle, lastWithDouble, lastWithNasal, lastWithShort);
    }
}
