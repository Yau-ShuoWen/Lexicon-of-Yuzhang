package com.shuowen.yuzong.Linguistics.pinyin;

import com.shuowen.yuzong.Linguistics.Format.CEDStyle;
import com.shuowen.yuzong.Linguistics.Scheme.*;
import com.shuowen.yuzong.util.text.StringTool;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.Range;
import com.shuowen.yuzong.util.err.InvalidPinyinException;

/**
 * 成都话拼音
 */
public class CEDPinyin extends UniPinyin<CEDStyle>
{
    protected CEDPinyin(SPinyin s)
    {
        super(s);
    }

    public static Maybe<CEDPinyin> tryOf(SPinyin s, boolean fromDatabase)
    {
        try
        {
            var p = fromDatabase ? s : CEDKeyboard.normalize(s);
            return Maybe.exist(new CEDPinyin(p));
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

            if (!py.matches(".*[aoeëiıuü].*"))
            {
                if (py.equals("m"))
                {
                    c.setMul("声母", "00", "韵尾", "3", "介母", "0", "中心元音", "0");
                    return c.toString();
                }
                else throw new IllegalArgumentException("没有主元音，但是不是特殊音节");
            }

            int idx = 1;
            c.set("声母", switch (StringTool.substring(py, 0, 1))
            {
                case "b" -> "01";
                case "p" -> "02";
                case "m" -> "03";
                case "f" -> "04";
                case "v" -> "05";
                case "d" -> "06";
                case "t" -> "07";
                case "n" -> "08";
                case "g" -> "09";
                case "k" -> "10";
                case "ŋ" -> "11";
                case "h" -> "12";
                case "j" -> "13";
                case "q" -> "14";
                case "ñ" -> "15";
                case "x" -> "16";
                case "z" -> "17";
                case "c" -> "18";
                case "s" -> "19";
                case "r" -> "20";
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
                case "i" -> "4";
                case "u" -> "5";
                case "n" -> "6";
                case "ŋ" -> "7";
                case "r" -> "8";
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
                case "ë" -> "3";
                case "e" -> "4";
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
        } catch (IndexOutOfBoundsException | IllegalArgumentException e)
        {
            throw new InvalidPinyinException("");
        }
    }

    public void checkEncodable()
    {

    }

    public void checkToneValid()
    {
        if (!Range.close(0, 7).contains(tone.getValueOrDefault(0)))
            throw new InvalidPinyinException("音调范围超出");
    }

    public int initCorner()
    {
        int[] fourCorner = {0, 1, 2, 3, 5, 0, 0, 0};// 阴平 阳平 阴上 阴去
        return fourCorner[tone.getValueOrDefault(0)];
    }

    public String initWeight()
    {
        return "";
    }

    @Override
    public String toString()
    {
        return String.format("默认的南昌话拼音：%s%s", syll,
                tone.handleIfExistAndGet(Object::toString, "")
        );
    }

    @Override
    public RPinyin toRPinyin(CEDStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY -> CEDDisplay.format(this);
            case KEYBOAD -> CEDKeyboard.format(this);
            case INTRO -> CEDIntro.format(this);
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return RPinyin.of(pinyin);
    }

    @Override
    public SPinyin toSPinyin(CEDStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY -> throw new IllegalArgumentException();
            case KEYBOAD -> CEDKeyboard.format(this);
            case INTRO -> CEDIntro.format(this);
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return SPinyin.of(pinyin);
    }

    public DPinyin toDPinyin(CEDStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY, KEYBOAD, INTRO -> throw new IllegalArgumentException();
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return DPinyin.of(pinyin);
    }

    private static class CEDDisplay
    {
        public static String format(CEDPinyin p)
        {
            String s = p.syll;

            if (s.endsWith("ië") || s.endsWith("uë") || s.endsWith("üë"))
            {
                s = s.replace("ë", "e");
            }
            s = decodeYiWuFront(s);
            s = PinyinCommon.d_ZCSR(s);
            s = PinyinCommon.d_Nh(s);
            s = PinyinCommon.d_Ng(s);
            s = PinyinCommon.d_Yu_display(s);
            s = PinyinCommon.d_Ao(s);

            if (p.tone.isEmpty() || p.tone.getValue() == 0) return s;
            else
            {
                char[] marks = {' ', '̄', '̣', '̀', '̌', '̄', '̣', '̄'};
                char t = marks[p.tone.getValue()]; // 前面检查过了

                if (s.contains("iu")) return s.replace("u", "u" + t);

                for (String i : "aoeêëiuü".split(""))
                    if (s.contains(i)) return s.replace(i, i + t);

                return s + t; // m
            }
        }
    }

    private static class CEDKeyboard
    {
        public static String format(CEDPinyin p)
        {
            String s = p.syll;

            if (s.endsWith("ië") || s.endsWith("uë") || s.endsWith("üë"))
            {
                s = s.replace("ë", "e");
            }
            s = decodeYiWuFront(s);
            s = PinyinCommon.d_ZCSR(s);
            s = PinyinCommon.d_Nh(s);
            s = PinyinCommon.d_Ng(s);
            s = PinyinCommon.d_Yu_keyboard(s);
            s = PinyinCommon.d_Ao(s);

            var t = p.tone;
            if (t.isEmpty() || t.getValue() == 0) return s;

            return s + switch (t.getValue())
            {
                case 1, 2, 3, 4 -> t.getValue();
                case 5 -> 22;
                case 6 -> 33;
                case 7 -> 44;
                default -> throw new IllegalArgumentException();
            };
        }

        public static SPinyin normalize(SPinyin p)
        {
            String s = p.getSyll();
            var t = p.getTone();

            s = PinyinCommon.e_ZCSR(s);
            s = PinyinCommon.e_Nh(s);
            s = PinyinCommon.e_Ng(s);
            s = PinyinCommon.e_Yu(s);
            s = PinyinCommon.e_Yi(s);
            s = PinyinCommon.e_Wu(s);
            s = PinyinCommon.e_Ao(s);
            if (s.endsWith("e")) s = s.replace("e", "ë");
            if (s.endsWith("ie") || s.endsWith("ue") || s.endsWith("üe"))
            {
                s = s.replace("e", "ë");
            }

            if (!t.isEmpty())
            {
                t = switch (t.getValue())
                {
                    case "1", "2", "3", "4" -> t;
                    case "22" -> Maybe.exist("5");
                    case "33" -> Maybe.exist("6");
                    case "44" -> Maybe.exist("7");
                    default -> throw new InvalidPinyinException("");
                };
            }

            return SPinyin.of(s, t);
        }
    }

    // 给零声母i u打标记
    protected static String decodeYiWuFront(String s)
    {
        s = switch (s)
        {
            case "i" -> "yi";
            case "ia" -> "ya";
            case "ië" -> "ye";
            case "iao" -> "yao";
            case "iu" -> "you";
            case "ian" -> "yan";
            case "in" -> "yin";
            case "iang" -> "yang";
            case "iong" -> "yong";
            case "ier" -> "yer";

            case "u" -> "wu";
            case "uë" -> "wue";
            case "uai" -> "wai";
            case "ui" -> "wei";
            case "uan" -> "wan";
            case "un" -> "wen";
            case "uang" -> "wang";
            case "uer" -> "wer";

            default -> s;
        };
        return s;
    }

    private static class CEDIntro
    {
        public static String format(CEDPinyin p)
        {
            String s = p.syll;

            s = s.replaceAll("([iuü])ë$", "$1e");
            s = decodeYiWuFront(s);
            s = PinyinCommon.d_ZCSR(s);
            s = PinyinCommon.d_Nh(s);
            s = PinyinCommon.d_Ng(s);
            s = PinyinCommon.d_Yu_keyboard(s);
            s = PinyinCommon.d_Ao(s);

            if (p.tone.isEmpty() || p.tone.getValue() == 0) return s;
            else
            {
                int T = p.tone.getValue();

                switch (p.tone.getValue())
                {
                    case 1, 3, 4, 5, 7 ->
                    {
                        char[] marks = {'_', '̄', '_', '́', '̌', '̄', ' ', '̄'};
                        char t = marks[T];

                        if (s.contains("iu")) return s.replace("u", "u" + t);

                        for (String i : "aoeêëiuü".split(""))
                            if (s.contains(i)) return s.replace(i, i + t);

                        return s + t; // m
                    }
                    case 2, 6 ->
                    {
                        char[] marks = {'+', '_', '↓', '_', '_', '_', '↓', '_'};
                        char t = marks[T];

                        return s + t;
                    }
                    default -> throw new IllegalArgumentException();
                }
            }
        }
    }
}
