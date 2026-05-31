package com.shuowen.yuzong.data.domain.Personal;

import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.Range;
import com.shuowen.yuzong.Tool.dataStructure.option.Alphabet;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class NumberTransfer
{
    public static String format(Alphabet alphabet, Language l, String funName, String args)
    {
        try
        {
            switch (alphabet)
            {
                case SuZhouCode ->
                {
                    return (String) SuzhouCode.class.getMethod(funName, String.class, Language.class)
                            .invoke(null, args, l);
                }
                case RomanNumber ->
                {
                    return (String) RomanNumber.class.getMethod(funName, String.class, Language.class)
                            .invoke(null, args, l);
                }
                case NumberSystem ->
                {
                    return (String) NumberSystem.class.getMethod(funName,String.class,Language.class)
                            .invoke(null, args, l);
                }

                default -> throw new RuntimeException();
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Maybe<BigInteger> toBigInt(String s)
    {
        try
        {
            return Maybe.exist(new BigInteger(s));
        } catch (Exception e)
        {
            return Maybe.nothing();
        }
    }

    private static Maybe<Integer> toInt(String s)
    {
        try
        {
            return Maybe.exist(Integer.valueOf(s));
        } catch (NumberFormatException e)
        {
            return Maybe.nothing();
        }
    }


    static class SuzhouCode
    {
        static char[][] map = {
                "〇〡〢〣〤〥〦〧〨〩".toCharArray(),
                "〇一二三〤〥〦〧〨〩".toCharArray(),
        };

        static Map<Character, Integer> revMap = new HashMap<>();

        static
        {
            for (char[] chars : map)
            {
                for (int j = 0; j < chars.length; j++)
                {
                    revMap.put(chars[j], j);
                }
            }
        }

        public static String encode(String str, Language l)
        {
            var maybe = toBigInt(str);
            if (maybe.isEmpty()) return ScTcText.get("數字格式錯誤", "数字格式错误", l);
            BigInteger num = maybe.getValue();

            String ans = "";
            boolean vertical = true;
            for (char c : num.toString().toCharArray())
            {
                if (Range.close('1', '3').contains(c))
                {
                    ans += map[vertical ? 0 : 1][c - '0'];
                    vertical = !vertical;
                }
                else
                {
                    ans += map[0][c - '0'];
                    vertical = true;
                }
            }
            return ans;
        }

        public static String decode(String str, Language l)
        {
            String ans = "";
            for (char c : str.toCharArray())
            {
                if (!revMap.containsKey(c)) return ScTcText.get("數字格式錯誤", "数字格式错误", l);
                ans += revMap.get(c);
            }
            return ans;
        }
    }

    static class RomanNumber
    {
        private static final int[] VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        private static final String[] ASCII = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        private static final String[] UNICODE = {"Ⅿ", "ⅭⅯ", "Ⅾ", "ⅭⅮ", "Ⅽ", "ⅩⅭ", "Ⅼ", "ⅩⅬ", "Ⅹ", "Ⅸ", "Ⅴ", "Ⅳ", "Ⅰ"};

        public static String encode(String str, Language l)
        {
            var maybe = toInt(str);
            if (maybe.isEmpty()) return ScTcText.get("數字格式錯誤", "数字格式错误", l);
            int num = maybe.getValue();

            if (!Range.close(1, 3999).contains(num))
                return ScTcText.get("傳統羅馬數字僅支持數字1-3999", "传统罗马数字仅支持数字1-3999", l);

            String ascii = "", unicode = "";
            for (int i = 0; i < VALUES.length; i++)
            {
                while (num >= VALUES[i])
                {
                    ascii += ASCII[i];
                    unicode += UNICODE[i];
                    num -= VALUES[i];
                }
            }

            String fmt = ScTcText.get("英文字母：%s\n專用符號：%s", "英文字母：%s\n专用符号：%s", l);

            return String.format(fmt, ascii, unicode);
        }
    }

    static class NumberSystem
    {
        private static String handle(String str, Language l, int base)
        {
            boolean valid = switch (base)
            {
                case 2 -> str.matches("^-?[01]+$");
                case 8 -> str.matches("^-?[0-7]+$");
                case 10 -> str.matches("^-?\\d+$");
                case 16 -> str.matches("^-?[0-9A-Fa-f]+$");
                default -> throw new RuntimeException("进制错误");
            };
            if (!valid) return ScTcText.get("數字格式錯誤", "数字格式错误", l);


            BigInteger val;
            try
            {
                val = new BigInteger(str, base);
            } catch (NumberFormatException e)
            {
                return ScTcText.get("數字格式錯誤", "数字格式错误", l);
            }

            String table = ScTcText.get(
                    """
                            |---|---|
                            |二進制|%s|
                            |八進制|%s|
                            |十進制|%s|
                            |十六進制|%s|""",
                    """
                            |---|---|
                            |二进制|%s|
                            |八进制|%s|
                            |十进制|%s|
                            |十六进制|%s|""",
                    l);

            return String.format(table,
                    val.toString(2),
                    val.toString(8),
                    val.toString(10),
                    val.toString(16).toUpperCase()
            );
        }


        // 二進制→其他進制
        public static String binary(String str, Language l)
        {
            return handle(str, l, 2);
        }

        // 八進制→其他進制
        public static String octal(String str, Language l)
        {
            return handle(str, l, 8);
        }

        // 十進制→其他進制
        public static String decimal(String str, Language l)
        {
            return handle(str, l, 10);
        }

        // 十六進制→其他進制
        public static String hexadecimal(String str, Language l)
        {
            return handle(str, l, 16);
        }
    }
}
