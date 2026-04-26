package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
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
    protected NamPinyin(SPinyin s)
    {
        super(s);
    }

    public static Maybe<NamPinyin> tryOf(SPinyin s, boolean fromDatabase)
    {
        try
        {
            var p = fromDatabase ? s : LacKeyboard.normalize(s);
            return Maybe.exist(new NamPinyin(p));
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
            String py = syll;
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
                    if (StringTool.charEquals(syll, 1, 'g'))
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

        if (!Objects.equals(syll, reverse))
            throw new InvalidPinyinException("没有正确逆推");
    }

    protected void checkToneValid()
    {
        var t = tone.getValueOrDefault(0); // 轻声不会影响无声调的判断

        if (!NumberTool.closeBetween(t, 0, 7)) throw new InvalidPinyinException("音调范围超出");

        boolean end = ObjectTool.existEqual(StringTool.back(syll), 't', 'k');
        if (NumberTool.closeBetween(t, 1, 5)) if (end) throw new InvalidPinyinException("非入声音调配对入声韵尾");
        if (NumberTool.closeBetween(t, 6, 7)) if (!end) throw new InvalidPinyinException("入声音调配对非入声韵尾");
    }

    protected int initCorner()
    {
        int[] fourCorner = {0, 1, 2, 3, 5, 6, 7, 8}; // 没有写错，没有4是因为南昌话没有「阳上」音
        return fourCorner[tone.getValueOrDefault(0)];
    }

    protected String initWeight()
    {
        return code + (tone.isValid() ? tone.getValue() : "");
    }

    @Override
    public String toString()
    {
        return String.format("默认的南昌话拼音：%s%s", syll,
                tone.handleIfExistAndGet(Object::toString, "")
        );
    }

    @Override
    protected RPinyin toRPinyin(NamStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY -> LacDisplay.format(this);
            case KEYBOAD -> LacKeyboard.format(this);
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return RPinyin.of(pinyin);
    }

    @Override
    protected SPinyin toSPinyin(NamStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY -> throw new IllegalArgumentException();
            case KEYBOAD -> LacKeyboard.format(this);
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return SPinyin.of(pinyin);
    }

    protected DPinyin toDPinyin(NamStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY, KEYBOAD -> throw new IllegalArgumentException();
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return DPinyin.of(pinyin);
    }

    /**
     * 展示格式工具类，单向
     */
    private static class LacDisplay
    {
        public static String format(NamPinyin p)
        {
            String s = p.syll;

            // 非常标准，三个双字母，一个ii
            s = s.replace("yu", "ü");
            s = s.replace("ee", "ẹ");
            s = s.replace("oe", "ọ");
            s = PinyinCommon.decodeZiiCiiSii(s);

            // 标音调
            if (p.tone.isEmpty() || p.tone.getValue() == 0) return s;
            else
            {
                char[] marks = {' ', '̀', '́', '̌', '̄', '̉', '̋', '̏'};
                char t = marks[p.tone.getValue()]; // 前面检查过了

                if (s.contains("iu")) return s.replace("u", "u" + t);

                for (String i : "aoọeẹiuü".split(""))
                    if (s.contains(i)) return s.replace(i, i + t);

                // 例外：没有主元音m n ng，只有ng要特殊处理
                if ("ng".equals(s)) return StringTool.insert(s, 1, t);
                else return s + t;
            }
        }
    }

    /**
     * 输入格式工具类，双向
     */
    private static class LacKeyboard
    {
        // 实现：只要处理ii的问题就可以了，其他不动

        public static String format(NamPinyin p)
        {
            String s = p.syll;
            var t = p.tone;

            s = PinyinCommon.decodeZiiCiiSii(s);

            return s + (t.isValid() ? t.getValue() : "");
        }

        public static SPinyin normalize(SPinyin p)
        {
            String s = p.getSyll();

            s = PinyinCommon.encodeZiCiSi(s);

            return SPinyin.of(s, p.getTone());
        }
    }


    public static SPinyin normalize(SPinyin pinyin)
    {
        String text = pinyin.getSyll();
        text = text.toLowerCase();

        text = PinyinCommon.encodeYiFront(text, false);
        text = PinyinCommon.encodeWuFront(text, false);
        text = PinyinCommon.encodeYuNotFront(text, true, false);
        text = PinyinCommon.encodeJuQuXu(text, false);// yu是正确写法，所以直接写不惩罚。

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

        return SPinyin.of(text, pinyin.getTone());
    }
}
