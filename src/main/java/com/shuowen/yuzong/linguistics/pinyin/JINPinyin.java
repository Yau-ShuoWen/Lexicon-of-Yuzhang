//package com.shuowen.yuzong.linguistics.pinyin;
//
//import com.shuowen.yuzong.linguistics.Format.JINStyle;
//import com.shuowen.yuzong.linguistics.util.*;
//import com.shuowen.yuzong.util.err.InvalidPinyinException;
//import com.shuowen.yuzong.util.text.StringTool;
//import com.shuowen.yuzong.util.tuple.Maybe;
//
//public class JINPinyin extends UniPinyin<JINStyle>
//{
//    protected JINPinyin(SPinyin s)
//    {
//        super(s);
//    }
//
//    public static Maybe<JINPinyin> tryOf(SPinyin s, boolean fromDatabase)
//    {
//        try
//        {
//            var p = fromDatabase ? s : s;//: WUHKeyboard.normalize(s);
//            return Maybe.exist(new JINPinyin(p));
//        } catch (InvalidPinyinException e)
//        {
//            return Maybe.nothing();
//        }
//    }
//
//    @Override
//    public String initCode()
//    {
//        try
//        {
//            FLCode c = new FLCode("声母:2,儿化:1,韵尾:1,介母:1,中心元音:1");
//            String py = syll;
//
//            if (!py.matches(".*[aoeëiıİuü].*"))
//            {
//                if (py.equals("ŋ"))
//                {
//                    c.setMul("声母", "0", "介母", "0", "中心元音", "6", "韵尾", "0");
//                    return c.toString();
//                }
//                else throw new IllegalArgumentException("没有主元音，但不是特殊音节");
//            }
//
//            int idx = 1;
//            c.set("声母", switch (StringTool.substring(py, 0, 1))
//            {
//                case "b" -> "01";
//                case "p" -> "02";
//                case "m" -> "03";
//                case "f" -> "04";
//                case "v" -> "05";
//                case "d" -> "06";
//                case "t" -> "07";
//                case "n" -> "08";
//                case "l" -> "09";
//                case "g" -> "10";
//                case "k" -> "11";
//                case "ŋ" -> "12";
//                case "h" -> "13";
//                case "j" -> "14";
//                case "q" -> "15";
//                case "ñ" -> "16";
//                case "x" -> "17";
//                case "ẑ" -> "18";
//                case "ĉ" -> "19";
//                case "ŝ" -> "20";
//                case "r" -> "21";
//                case "z" -> "22";
//                case "c" -> "23";
//                case "s" -> "24";
//                default ->
//                {
//                    idx = 0;
//                    yield "00";
//                }
//            });
//            py = py.substring(idx);
//
//
//            if (py.isEmpty()) throw new InvalidPinyinException("此处拼音结构不完整");
//
//            if (py.equals("İ"))
//            {
//                c.setMul("介母", "0", "中心元音", "0", "韵尾", "1");
//                return c.toString();
//            }
//            if (py.equals("ı"))
//            {
//                c.setMul("介母", "0", "中心元音", "0", "韵尾", "2");
//                return c.toString();
//            }
//
//            idx = 1;
//            c.set("介母", switch (StringTool.substring(py, 0, 1)) // 删掉了开头的就是现在的
//            {
//                case "i" -> "1";
//                case "u" -> "2";
//                case "ü" -> "3";
//                default ->
//                {
//                    idx = 0;
//                    yield "0";
//                }
//            });
//            py = py.substring(idx);
//
//            idx = 1;
//            c.set("韵尾", switch (StringTool.substring(py, py.length() - 1))
//            {
//                case "i" -> "3";
//                case "u" -> "4";
//                case "n" -> "5";
//                case "ŋ" -> "6";
//                case "r" -> "7";
//                default ->
//                {
//                    idx = 0;
//                    yield "0";
//                }
//            });
//            py = py.substring(0, py.length() - idx);
//
//            c.set("中心元音", switch (py)
//            {
//                case "a" -> "1";
//                case "o" -> "2";
//                case "e" -> "3";
//                case "ë" -> "4";
//                default ->
//                {
//                    // 没有在已有的情况下识别到主元音
//                    // py为空，如iu i为介母 u为韵尾，正常置空
//                    // 不然说明剩下的格式不正确
//                    if (!py.isEmpty()) throw new IndexOutOfBoundsException();
//                    else yield "0";
//                }
//            });
//
//            return c.toString();
//
//        } catch (IndexOutOfBoundsException | IllegalArgumentException e)
//        {
//            throw new InvalidPinyinException("无效武汉话拼音");
//        }
//    }
//
//    @Override
//    public void checkToneValid()
//    {
//
//    }
//
//    @Override
//    public void checkEncodable()
//    {
//
//    }
//
//    @Override
//    public int initCorner()
//    {
//        return 0;
//    }
//
//    @Override
//    public String initWeight()
//    {
//        return "";
//    }
//
//    @Override
//    public RPinyin toRPinyin(JINStyle params)
//    {
//        return null;
//    }
//
//    @Override
//    public SPinyin toKeyboardPinyin(JINStyle params)
//    {
//        return null;
//    }
//
//    @Override
//    public DPinyin toDatabasePinyin(JINStyle params)
//    {
//        return null;
//    }
//
//    @Override
//    public PinyinBlock format()
//    {
//        return null;
//    }
//}
