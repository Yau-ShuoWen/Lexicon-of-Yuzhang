package com.shuowen.yuzong.Linguistics.pinyin;

import com.shuowen.yuzong.Linguistics.Format.LACStyle;
import com.shuowen.yuzong.Linguistics.Scheme.*;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.text.StringTool;
import com.shuowen.yuzong.util.tuple.Range;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.tuple.Maybe;

import java.util.Map;
import java.util.Objects;

/**
 * 南昌话拼音方案
 */
public class LACPinyin extends UniPinyin<LACStyle>
{
    protected LACPinyin(SPinyin s)
    {
        super(s);
    }

    public static Maybe<LACPinyin> tryOf(SPinyin s, boolean fromDatabase)
    {
        try
        {
            var p = fromDatabase ? s : LACKeyboard.normalize(s);
            return Maybe.exist(new LACPinyin(p));
        } catch (InvalidPinyinException e)
        {
            return Maybe.nothing();
        }
    }

    public String initCode()
    {
        try
        {
            FLCode c = new FLCode("声母:2,韵尾:1,介母:1,中心元音:1");
            String py = syll;

            if (!py.matches(".*[aoọeẹiıuü].*"))
            {
                switch (py)
                {
                    case "m" -> c.setMul("声母", "00", "介母", "0", "中心元音", "7", "韵尾", "0");
                    case "n" -> c.setMul("声母", "00", "介母", "0", "中心元音", "8", "韵尾", "0");
                    case "ŋ" -> c.setMul("声母", "00", "介母", "0", "中心元音", "9", "韵尾", "0");
                    default -> throw new IllegalArgumentException("没有主元音，但不是特殊音节");
                }
                return c.toString();
            }

            int idx;


            // 声母：特殊的地方只有
            // 1. n和ng都是n开头，需要具体区分
            // 2. 除了零声母识别，ng识别长度为2，其他都是1位（所以统一idx=1，其他的调整）
            idx = 1;
            c.set("声母", switch (StringTool.substring(py, 0, 1))
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
                case "ŋ" -> "10";
                case "h" -> "11";
                case "j" -> "12";
                case "q" -> "13";
                case "n" -> "14";
                case "x" -> "15";
                case "z" -> "16";
                case "c" -> "17";
                case "s" -> "18";
                default ->
                {
                    idx = 0;
                    yield "00";
                }
            });
            py = py.substring(idx);

            if (py.isEmpty()) throw new InvalidPinyinException("此处拼音结构不完整");

            if (py.equals("ı"))
            {
                c.setMul("介母", "0", "中心元音", "0", "韵尾", "1");
                return c.toString();
            }

            idx = 1;
            c.set("介母", switch (StringTool.substring(py, 0, 1)) // 删掉了开头的就是现在的
            {
                case "i" -> "1";
                case "u" -> "2";
                case "ü" -> "3";
                default ->
                {
                    idx = 0;
                    yield "0";
                }
            });
            py = py.substring(idx);

            idx = 1;
            c.set("韵尾", switch (StringTool.substring(py, py.length() - 1))
            {
                case "i" -> "3";
                case "u" -> "4";
                case "n" -> "5";
                case "ŋ" -> "6";
                case "t" -> "7";
                case "k" -> "8";
                case "l" -> "9";
                default ->
                {
                    idx = 0;
                    yield "0";
                }
            });
            py = py.substring(0, py.length() - idx);


            c.set("中心元音", switch (py)
            {
                case "a" -> "1";
                case "o" -> "2";
                case "e" -> "3";
                case "ẹ" -> "4";
                case "ọ" -> "5";
                case "u" -> "6";
                default ->
                {
                    // 没有在已有的情况下识别到主元音
                    // py为空，如iu i为介母 u为韵尾，正常置空
                    // 不然说明剩下的格式不正确
                    if (!py.isEmpty()) throw new IndexOutOfBoundsException();
                    else yield "0";
                }
            });

            return c.toString();
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) // 这里面拼音出现了任何错误，就认为是无效的，所以里面可以大胆sub和charAt
        {
            throw new InvalidPinyinException("拼音编码出现异常");
        }
    }

    public void checkEncodable()
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
            case "10" -> "ŋ";
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
            case '3' -> "ü";
            default -> "";
        } + switch (c.charAt(4))
        {
            case '1' -> "a";
            case '2' -> "o";
            case '3' -> "e";
            case '4' -> "ẹ";
            case '5' -> "ọ";
            case '6' -> "u";
            case '7' -> "m";
            case '8' -> "n";
            case '9' -> "ŋ";
            default -> "";
        } + switch (c.charAt(2))
        {
            case '1' -> "ı";
            case '3' -> "i";
            case '4' -> "u";
            case '5' -> "n";
            case '6' -> "ŋ";
            case '7' -> "t";
            case '8' -> "k";
            case '9' -> "l";
            default -> "";
        };

        if (!Objects.equals(syll, reverse))
            throw new InvalidPinyinException("没有正确逆推");
    }

    public void checkToneValid()
    {
        var t = tone.getValueOrDefault(0); // 轻声不会影响无声调的判断

        if (!Range.close(0, 7).contains(t)) throw new InvalidPinyinException("音调范围超出");

        boolean end = ObjectTool.existEqual(StringTool.back(syll), 't', 'k', 'l');
        if (Range.close(1, 5).contains(t)) if (end) throw new InvalidPinyinException("非入声音调配对入声韵尾");
        if (Range.close(6, 7).contains(t)) if (!end) throw new InvalidPinyinException("入声音调配对非入声韵尾");
    }

    public int initCorner()
    {
        int[] fourCorner = {0, 1, 2, 3, 5, 6, 7, 8}; // 没有写错，没有4是因为南昌话没有「阳上」音
        return fourCorner[tone.getValueOrDefault(0)];
    }

    public String initWeight()
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
    public RPinyin toRPinyin(LACStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY -> LACDisplay.format(this);
            case KEYBOAD -> LACKeyboard.format(this);
            case INTRO -> LACIntro.format(this);
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return RPinyin.of(pinyin);
    }

    @Override
    public SPinyin toSPinyin(LACStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY, INTRO -> throw new IllegalArgumentException();
            case KEYBOAD -> LACKeyboard.format(this);
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return SPinyin.of(pinyin);
    }

    public DPinyin toDPinyin(LACStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY, KEYBOAD, INTRO -> throw new IllegalArgumentException();
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return DPinyin.of(pinyin);
    }

    /**
     * 展示格式工具类，单向
     */
    private static class LACDisplay
    {
        public static String format(LACPinyin p)
        {
            String s = p.syll;

            s = PinyinCommon.d_ZCSR(s);
            s = PinyinCommon.d_Ng(s);

            // 标音调
            if (p.tone.isEmpty() || p.tone.getValue() == 0) return s;
            else
            {
                char[] marks = {'_', '̀', '́', '̌', '̄', '̉', '̋', '̏'};
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
    private static class LACKeyboard
    {
        // 实现：只要处理ii的问题就可以了，其他不动

        public static String format(LACPinyin p)
        {
            String s = p.syll;
            var t = p.tone;

            s = s.replace("ü", "yu");
            s = s.replace("ẹ", "ee");
            s = s.replace("ọ", "oe");
            s = PinyinCommon.d_ZCSR(s);
            s = PinyinCommon.d_Ng(s);

            return s + (t.isValid() ? t.getValue() : "");
        }

        private final static Map<Character, String> tones = Map.of(
                '̀', "1",
                '́', "2",
                '̌', "3",
                '̄', "4",
                '̉', "5",
                '̋', "6",
                '̏', "7"
        );

        public static SPinyin normalize(SPinyin p)
        {
            if (p.getTone().isEmpty()) p = ToneParser.parse(p.getSyll(), tones, "0");

            String s = p.getSyll().toLowerCase();

            // 标准替换
            s = s.replace("ee", "ẹ");
            s = s.replace("oe", "ọ");
            s = PinyinCommon.e_ZCSR(s);
            s = PinyinCommon.e_Ng(s);

            // i u ü 问题
            s = PinyinCommon.e_Yi(s);
            s = PinyinCommon.e_Wu(s);
            s = PinyinCommon.e_JQX_Ü_V_Yu_U(s);
            s = PinyinCommon.e_Ü_V_Yu(s);


            // 双韵母的模糊处理
            // 匹配：普通话常见但是不符合的： ao->au  iau->ieu  ou->eu  iou->iu uei->ui
            if (s.contains("ao")) s = s.replace("ao", "au");
            if (s.contains("iau")) s = s.replace("iau", "ieu");
            if (s.contains("ou"))
            {
                if (s.contains("iou"))
                    s = s.replace("iou", "iu");
                else s = s.replace("ou", "eu");
            }
            if (s.contains("uei")) s = s.replace("uei", "ui");

            // 鼻韵母的模糊处理
            // 匹配：普通话常见但是不符合的： ian->ien  üan->üon  uen->un
            s = s.replaceAll("ian$", "ien"); // 用$防止iang
            s = s.replace("üan", "üon");
            s = s.replace("uen", "un");

            var t = p.getTone();

            if (!t.isEmpty())
            {
                t = switch (t.getValue())
                {
                    case "0", "1", "2", "3", "4", "5", "6", "7" -> Maybe.exist(t.getValue());
                    default -> throw new InvalidPinyinException("");
                };
            }

            return SPinyin.of(s, t);
        }
    }

    /**
     * 简化拼音
     */
    private static class LACIntro
    {
        public static String format(LACPinyin p)
        {
            String s = p.syll;

            s = s.replace("ien", "ian").replace("üon", "üan");
            s = s.replaceAll("[tk]$", ""); // 删除入声韵尾
            s = handleYW(s);
            s = PinyinCommon.d_Yu_display(s);
            s = s.replace("ẹ", "e");
            s = s.replace("ọ", "o");
            s = PinyinCommon.d_ZCSR(s);
            s = PinyinCommon.d_Ng(s);

            // 标音调
            if (p.tone.isEmpty() || p.tone.getValue() == 0) return s;
            else
            {
                int T = p.tone.getValue();

                // 前四个声调是和普通话一样的
                if (Range.close(1, 4).contains(T))
                {
                    char[] marks = {'̀', '́', '̌', '̄'};
                    char t = marks[T - 1];

                    if (s.contains("yi+wu")) return s.replace("u", "u" + t);

                    for (String i : "aoọeẹiuü".split(""))
                        if (s.contains(i)) return s.replace(i, i + t);

                    // 例外：没有主元音m n ng，只有ng要特殊处理
                    if ("ng".equals(s)) return StringTool.insert(s, 1, t);
                    else return s + t;
                }
                else
                {
                    char[] marks = {'↘', '↑', '↓'};
                    char t = marks[T - 5];

                    return s + t;
                }
            }
        }

        /**
         * 合理添加yw，使得看起来更符合普通话规律
         */
        private static String handleYW(String s)
        {
            char c = s.charAt(0);
            if (c == 'i')
            {
                // i ->yi it->yit iu->yiu in->yin
                if (ObjectTool.existEqual(s, "i", "it", "in"))
                    s = "y" + s;
                else if (s.equals("iu")) s = "yi+wu";
                else s = "y" + s.substring(1);
            }
            if (c == 'u')
            {
                if (s.length() >= 2 && ObjectTool.existEqual(s.charAt(1), 'a', 'o'))
                    s = "w" + s.substring(1);
                else if (s.equals("ui")) s = "wu+yi";
                else s = "w" + s;
            }
            return s;
        }
    }
}
