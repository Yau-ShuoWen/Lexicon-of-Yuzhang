package com.shuowen.yuzong.Linguistics.pinyin;

import com.shuowen.yuzong.Linguistics.util.*;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.data.domain.IPA.PinyinMode;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.text.StringTool;
import com.shuowen.yuzong.util.tuple.Maybe;

public class WUHPinyin extends UniPinyin
{
    protected WUHPinyin(SplitedPinyin s)
    {
        super(s, Dialect.WUH);
    }

    public static Maybe<WUHPinyin> tryOf(SplitedPinyin p)
    {
        try
        {
            return Maybe.exist(new WUHPinyin(p));
        } catch (InvalidPinyinException e)
        {
            return Maybe.nothing();
        }
    }

    public static Maybe<WUHPinyin> tryOf(KeyboardPinyin p)
    {
        return tryOf(WUHKeyboard.normalize(p));
    }

    @Override
    public String initCode()
    {
        try
        {
            FLCode c = new FLCode("声母:2,韵尾:1,介母:1,中心元音:1");
            String py = syll;

            if (!py.matches(".*[aoeëiıuưü].*"))
            {
                switch (py)
                {
                    case "m" -> c.setMul("声母", "0", "介母", "0", "中心元音", "7", "韵尾", "0");
                    case "n" -> c.setMul("声母", "0", "介母", "0", "中心元音", "8", "韵尾", "0");
                    case "ŋ" -> c.setMul("声母", "0", "介母", "0", "中心元音", "9", "韵尾", "0");
                    default -> throw new IllegalArgumentException("没有主元音，但不是特殊音节");
                }
                return c.toString();
            }

            int idx = 1;
            c.set("声母", switch (StringTool.substring(py, 0, 1))
            {
                case "b" -> "01";
                case "p" -> "02";
                case "m" -> "03";
                case "f" -> "04";
                case "d" -> "05";
                case "t" -> "06";
                case "n" -> "07";
                case "g" -> "08";
                case "k" -> "09";
                case "ŋ" -> "10";
                case "h" -> "11";
                case "j" -> "12";
                case "q" -> "13";
                case "x" -> "14";
                case "z" -> "15";
                case "c" -> "16";
                case "s" -> "17";
                case "r" -> "18";
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
            if (py.equals("ẹ"))
            {
                c.setMul("介母", "0", "中心元音", "0", "韵尾", "2");
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
                case "ë" -> "4";
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
            throw new InvalidPinyinException("无效武汉话拼音");
        }
    }

    @Override
    public void checkToneValid()
    {

    }

    @Override
    public void checkEncodable()
    {

    }

    @Override
    public int initCorner()
    {
        return 0;
    }

    @Override
    public String initWeight()
    {
        return "";
    }

    @Override
    public RPinyin toRPinyin()
    {
        return null;
    }

    @Override
    public KeyboardPinyin toKeyboardPinyin()
    {
        return null;
    }

    @Override
    public DatabasePinyin toDatabasePinyin()
    {
        return null;
    }

    @Override
    public PinyinBlock format(PinyinMode md)
    {
        return null;
    }

    private static class WUHDisplay
    {
//        public static String format(WUHPinyin p)
//        {
//            String s = p.syll;
//
//            s = s.replaceAll("(iü)ë$", "$1e");
//            s = s.replace("ë", "ae");
//            s = PinyinCommon.d_Ao(s);
//            s = PinyinCommon.d_ZCSR(s);
////            s=PinyinCommon.
//
//          //  s.replace("")
//        }
    }
//    /**
//     * 展示格式工具类，单向
//     */
//    private static class LACDisplay
//    {
//        public static String format(LACPinyin p)
//        {
//            String s = p.syll;
//
//            // 非常标准，三个双字母，一个ii
//            s = s.replace("yu", "ü");
//            s = s.replace("ee", "ẹ");
//            s = s.replace("oe", "ọ");
//            s = PinyinCommon.decodeZiiCiiSii(s);
//
//            // 标音调
//            if (p.tone.isEmpty() || p.tone.getValue() == 0) return s;
//            else
//            {
//                char[] marks = {'?', '̀', '́', '̌', '̄', '̉', '̋', '̏'};
//                char t = marks[p.tone.getValue()]; // 前面检查过了
//
//                if (s.contains("iu")) return s.replace("u", "u" + t);
//
//                for (String i : "aoọeẹiuü".split(""))
//                    if (s.contains(i)) return s.replace(i, i + t);
//
//                // 例外：没有主元音m n ng，只有ng要特殊处理
//                if ("ng".equals(s)) return StringTool.insert(s, 1, t);
//                else return s + t;
//            }
//        }
//    }

    private static class WUHKeyboard
    {
        public static SplitedPinyin normalize(KeyboardPinyin p)
        {
            String s = p.getSyll().toLowerCase();

            s = PinyinCommon.e_Yi(s);          // 丢掉开头的 y 和 yi 为 i
            s = PinyinCommon.e_Wu(s);          // 丢掉开头的 w 和 wu 为 u
            s = PinyinCommon.e_JQX_Ü_V_Yu_U(s);// 丢掉JQX后的 ü v yu u 改为 yu
            s = PinyinCommon.e_Ü_V_Yu(s);      // 丢掉其他地方的 ü v yu 改为 yu
            s = PinyinCommon.e_ZCSR(s);        // 特殊编码 ZCS 后的 i
            s = PinyinCommon.d_Ng(s);          //
            s = PinyinCommon.e_Ao(s);          // ao 转 au

            s = s.replace("ae", "ë");
            s = s.replaceAll("(iü)e$", "$1ë");

            var t = p.getTone();

            if (!t.isEmpty())
            {
                t = switch (t.getValue())
                {
                    case "1", "2", "3", "4" -> t;
                    default -> throw new InvalidPinyinException("");
                };
            }
            return SplitedPinyin.of(s, t);
        }
    }

    private static class WUHTool
    {
      //  public static String e_()
    }
}
