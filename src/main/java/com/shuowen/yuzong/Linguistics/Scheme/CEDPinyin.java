package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.CEDStyle;
import com.shuowen.yuzong.Tool.JavaUtilExtend.StringTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.Range;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;

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

    protected String initCode()
    {
        try
        {

            String ans = "";
            String py = syll;

            if (!py.matches(".*[aoeiu].*"))
            {
                if (py.equals("m")) return "000060";
                else throw new IllegalArgumentException("没有主元音，但是不是特殊音节");
            }

            boolean erlize;
            if (py.endsWith("-er"))
            {
                erlize = true;
                py = py.substring(0, py.length() - 3);
            }
            else
            {
                erlize = false;
            }

            int idx;

            idx = 1;
            ans += switch (StringTool.substring(py, 0, 1))
            {
                case "b" -> "01";
                case "p" -> "02";
                case "m" -> "03";
                case "f" -> "04";
                case "d" -> "05";
                case "t" -> "06";
                case "n" ->
                {
                    if (py.charAt(1) == 'g')
                    {
                        idx = 2;
                        yield "10";
                    }
                    else if (py.charAt(1) == 'h')
                    {
                        idx = 2;
                        yield "14";
                    }
                    else yield "07";
                }
                case "g" -> "08";
                case "k" -> "09";
                case "h" -> "11";
                case "j" -> "12";
                case "q" -> "13";
                case "x" -> "15";
                case "z" -> "16";
                case "c" -> "17";
                case "s" -> "18";
                case "r" -> "19";
                default ->
                {
                    idx = 0;
                    yield "00";
                }
            };
            py = py.substring(idx);

            if (py.isEmpty()) throw new InvalidPinyinException("此处拼音结构不完整");

            ans += switch (py)
            {
                case "ii" -> "100";
                case "er" -> "200";

                case "i" -> "010";
                case "u" -> "020";
                case "yu" -> "030";

                case "a" -> "001";
                case "o" -> "002";
                case "e" -> "003";
                case "ia" -> "011";
                case "ie" -> "013";
                case "ua" -> "021";
                case "ue" -> "023";
                case "yuo" -> "032";
                case "yue" -> "033";

                case "ai" -> "401";
                case "ei" -> "404";
                case "iei" -> "414";
                case "uai" -> "421";
                case "ui" -> "424";
                case "ao" -> "501";
                case "ou" -> "505";
                case "iao" -> "511";
                case "iu" -> "515";

                case "an" -> "601";
                case "en" -> "605";
                case "ian" -> "611";
                case "in" -> "615";
                case "uan" -> "621";
                case "un" -> "625";
                case "yuan" -> "631";
                case "yun" -> "635";

                case "ang" -> "701";
                case "ong" -> "702";
                case "iang" -> "711";
                case "uang" -> "721";
                case "yuong" -> "732";

                default -> throw new InvalidPinyinException("");
            };

            return ans + (erlize ? 1 : 0);

        } catch (IndexOutOfBoundsException | IllegalArgumentException e)
        {
            throw new InvalidPinyinException("");
        }
    }

    protected void checkEncodable()
    {

    }

    protected void checkToneValid()
    {
        if (!Range.close(0, 6).contains(tone.getValueOrDefault(0)))
            throw new InvalidPinyinException("音调范围超出");
    }

    protected int initCorner()
    {
        int[] fourCorner = {0, 1, 2, 3, 5};// 阴平 阳平 阴上 阴去
        return fourCorner[tone.getValueOrDefault(0)];
    }

    protected String initWeight()
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
    protected RPinyin toRPinyin(CEDStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY -> CedDisplay.format(this);
            case KEYBOAD -> CEDKeyboard.format(this);
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return RPinyin.of(pinyin);
    }

    @Override
    protected SPinyin toSPinyin(CEDStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY -> throw new IllegalArgumentException();
            case KEYBOAD -> CEDKeyboard.format(this);
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return SPinyin.of(pinyin);
    }

    protected DPinyin toDPinyin(CEDStyle p)
    {
        String pinyin = switch (p.getStyle())
        {
            case DISPALY, KEYBOAD -> throw new IllegalArgumentException();
            case DEBUG -> syll + tone.handleIfExistAndGet(Object::toString, "");
        };
        return DPinyin.of(pinyin);
    }

    private static class CedDisplay
    {
        public static String format(CEDPinyin p)
        {
            String s = p.syll;

            s = decodeYiWuFront(s);
            s = PinyinCommon.decodeZiiCiiSii(s);
            s = PinyinCommon.decodeJyuQyuXyu(s);
            s = PinyinCommon.decodeYuNotFront(s, false);
            s = s.replace("r", "rh");
            if (p.code.charAt(4) == '3') s = s.replace("e", "ê");

            if (p.tone.isEmpty() || p.tone.getValue() == 0) return s;
            else
            {
                char[] marks = {' ', '̄', '̣', '̀', '̌', '̄', '̣', '̄'};
                char t = marks[p.tone.getValue()]; // 前面检查过了

                if (s.contains("iu")) return s.replace("u", "u" + t);

                for (String i : "aoêeiuü".split(""))
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
            var t = p.tone;

            s = decodeYiWuFront(s);
            s = PinyinCommon.decodeZiiCiiSii(s);
            s = PinyinCommon.decodeJyuQyuXyu(s);
            s = PinyinCommon.decodeYuNotFront(s, true);
            s = s.replace("r", "rh");

            return s + (t.isValid() ? t.getValue() : "");
        }

        public static SPinyin normalize(SPinyin p)
        {
            String s = p.getSyll();
            var t = p.getTone();

            s = PinyinCommon.encodeZiCiSi(s);
            s = PinyinCommon.encodeYiFront(s, true);
            s = PinyinCommon.encodeWuFront(s, true);
            s = PinyinCommon.encodeYuNotFront(s, true, true);
            s = PinyinCommon.encodeJuQuXu(s, true);
            s = s.replace("rh", "r");

            if (!t.isEmpty())
            {
                t = switch (t.getValue())
                {
                    case "1", "2", "3", "4" -> t;
                    case "2*" -> Maybe.exist("5");
                    case "3*" -> Maybe.exist("6");
                    case "4*" -> Maybe.exist("7");
                    default -> throw new InvalidPinyinException("");
                };
            }

            return SPinyin.of(s, t);
        }
    }

    // 给零声母i u打标记
    private static String decodeYiWuFront(String s)
    {
        s = switch (s)
        {
            case "i" -> "yi";
            case "ia" -> "ya";
            case "ie" -> "ye";
            case "iao" -> "yao";
            case "iu" -> "you";
            case "ian" -> "yan";
            case "in" -> "yin";
            case "iang" -> "yang";
            case "iong" -> "yong";
            case "u" -> "u";
            case "ue" -> "ue";
            case "uai" -> "wai";
            case "ui" -> "wei";
            case "uan" -> "wan";
            case "un" -> "wen";
            case "uang" -> "wang";
            default -> s;
        };
        return s;
    }

    public static SPinyin normalize(SPinyin pinyin)
    {
        String text = pinyin.getSyll();
        text = text.toLowerCase();

//        if (text.contains("yo")) text = text.replace("yo", "yuo");
//        if (text.contains("io")) text = text.replace("io", "yuo");

        return pinyin;
    }
}
