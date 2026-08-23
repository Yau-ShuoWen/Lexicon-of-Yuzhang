package com.shuowen.yuzong.ysw.linguistic;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RomajiToHiragana
{
    private static final Map<String, String> table = new HashMap<>();

    static
    {
        String[][] data = {
                {"a", "あ"}, {"i", "い"}, {"u", "う"}, {"e", "え"}, {"o", "お"},
                {"ka", "か"}, {"ki", "き"}, {"ku", "く"}, {"ke", "け"}, {"ko", "こ"},
                {"sa", "さ"}, {"shi", "し"}, {"su", "す"}, {"se", "せ"}, {"so", "そ"},
                {"ta", "た"}, {"chi", "ち"}, {"tsu", "つ"}, {"te", "て"}, {"to", "と"},
                {"na", "な"}, {"ni", "に"}, {"nu", "ぬ"}, {"ne", "ね"}, {"no", "の"},
                {"ha", "は"}, {"hi", "ひ"}, {"fu", "ふ"}, {"he", "へ"}, {"ho", "ほ"},
                {"ma", "ま"}, {"mi", "み"}, {"mu", "む"}, {"me", "め"}, {"mo", "も"},
                {"ya", "や"}, {"yu", "ゆ"}, {"yo", "よ"},
                {"ra", "ら"}, {"ri", "り"}, {"ru", "る"}, {"re", "れ"}, {"ro", "ろ"},
                {"wa", "わ"}, {"wo", "を"},

                {"ga", "が"}, {"gi", "ぎ"}, {"gu", "ぐ"}, {"ge", "げ"}, {"go", "ご"},
                {"za", "ざ"}, {"ji", "じ"}, {"zu", "ず"}, {"ze", "ぜ"}, {"zo", "ぞ"},
                {"da", "だ"}, {"de", "で"}, {"do", "ど"},
                {"ba", "ば"}, {"bi", "び"}, {"bu", "ぶ"}, {"be", "べ"}, {"bo", "ぼ"},
                {"pa", "ぱ"}, {"pi", "ぴ"}, {"pu", "ぷ"}, {"pe", "ぺ"}, {"po", "ぽ"},

                {"kya", "きゃ"}, {"kyu", "きゅ"}, {"kyo", "きょ"},
                {"sha", "しゃ"}, {"shu", "しゅ"}, {"sho", "しょ"},
                {"cha", "ちゃ"}, {"chu", "ちゅ"}, {"cho", "ちょ"},
                {"nya", "にゃ"}, {"nyu", "にゅ"}, {"nyo", "にょ"},
                {"hya", "ひゃ"}, {"hyu", "ひゅ"}, {"hyo", "ひょ"},
                {"mya", "みゃ"}, {"myu", "みゅ"}, {"myo", "みょ"},
                {"rya", "りゃ"}, {"ryu", "りゅ"}, {"ryo", "りょ"},
                {"gya", "ぎゃ"}, {"gyu", "ぎゅ"}, {"gyo", "ぎょ"},
                {"bya", "びゃ"}, {"byu", "びゅ"}, {"byo", "びょ"},
                {"pya", "ぴゃ"}, {"pyu", "ぴゅ"}, {"pyo", "ぴょ"},

                // 小假名
                {"xa", "ぁ"}, {"xi", "ぃ"}, {"xu", "ぅ"}, {"xe", "ぇ"}, {"xo", "ぉ"},
                {"la", "ぁ"}, {"li", "ぃ"}, {"lu", "ぅ"}, {"le", "ぇ"}, {"lo", "ぉ"},
                {"xtsu", "っ"}, {"ltsu", "っ"},
                {"xya", "ゃ"}, {"xyu", "ゅ"}, {"xyo", "ょ"},
                {"lya", "ゃ"}, {"lyu", "ゅ"}, {"lyo", "ょ"},

                // 基础变体
                {"si", "し"}, {"shi", "し"},
                {"ti", "ち"}, {"chi", "ち"},
                {"tu", "つ"}, {"tsu", "つ"},
                {"hu", "ふ"}, {"fu", "ふ"},
                {"zi", "じ"}, {"ji", "じ"},
                {"di", "ぢ"},
                {"du", "づ"},

                // c 扩展
                {"ca", "か"}, {"ci", "し"}, {"cu", "く"}, {"ce", "せ"}, {"co", "こ"},
                //　
                {"qa", "くぁ"}, {"qi", "くぃ"}, {"qu", "く"}, {"qe", "くぇ"}, {"qo", "くぉ"},

                // 外来音
                {"fa", "ふぁ"}, {"fi", "ふぃ"}, {"fe", "ふぇ"}, {"fo", "ふぉ"},
                {"va", "ゔぁ"}, {"vi", "ゔぃ"}, {"ve", "ゔぇ"}, {"vo", "ゔぉ"},
                {"she", "しぇ"}, {"je", "じぇ"}, {"che", "ちぇ"},
                {"tsa", "つぁ"}, {"tsi", "つぃ"}, {"tse", "つぇ"}, {"tso", "つぉ"},
                {"thi", "てぃ"}, {"dhi", "でぃ"},
                {"twi", "とぃ"}, {"dwi", "どぃ"},
                {"kwa", "くぁ"}, {"gwa", "ぐぁ"}
        };

        for (String[] x : data)
        {
            table.put(x[0], x[1]);
        }
    }


    public static String convert(String input)
    {
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < input.length())
        {

            // 促音：重复辅音，例如 kitta
            if (i + 1 < input.length()
                    && input.charAt(i) == input.charAt(i + 1)
                    && "bcdfghjklmpqrstvwxyz".indexOf(input.charAt(i)) >= 0)
            {
                result.append("っ");
                i++;
                continue;
            }

            // n + 非元音 = ん
            if (input.charAt(i) == 'n')
            {
                if (i + 1 == input.length() || "aiueo".indexOf(input.charAt(i + 1)) == -1)
                {
                    result.append("ん");
                    i++;
                    continue;
                }
            }


            boolean matched = false;

            // 优先匹配3个字符
            for (int len = 5; len >= 1; len--)
            {
                if (i + len <= input.length())
                {
                    String part = input.substring(i, i + len);
                    if (table.containsKey(part))
                    {
                        result.append(table.get(part));
                        i += len;
                        matched = true;
                        break;
                    }
                }
            }

            if (!matched)
            {
                result.append(input.charAt(i));
                i++;
            }
        }

        return result.toString();
    }


    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("输入罗马字：");

        while (true)
        {
            String s = scanner.nextLine();

            if (s.equals("exit"))
                break;

            System.out.println(convert(s));
        }

        scanner.close();
    }
}