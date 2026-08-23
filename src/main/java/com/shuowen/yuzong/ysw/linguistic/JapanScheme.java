package com.shuowen.yuzong.ysw.linguistic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JapanScheme
{
    private static final Map<String, String> ROMAJI_TO_HIRAGANA = new HashMap<>();

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
                {"wa", "わ"}, {"wi", "うぃ"}, {"we", "うぇ"}, {"wo", "を"},
                {"nn", "ん"},

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
                {"ja", "じゃ"}, {"ju", "じゅ"}, {"jo", "じょ"},
                {"bya", "びゃ"}, {"byu", "びゅ"}, {"byo", "びょ"},
                {"pya", "ぴゃ"}, {"pyu", "ぴゅ"}, {"pyo", "ぴょ"},

                {"xa", "ぁ"}, {"xi", "ぃ"}, {"xu", "ぅ"}, {"xe", "ぇ"}, {"xo", "ぉ"},
                {"la", "ぁ"}, {"li", "ぃ"}, {"lu", "ぅ"}, {"le", "ぇ"}, {"lo", "ぉ"},
                {"xwa", "ゎ"}, {"lwa", "ゎ"},
                {"xtsu", "っ"}, {"ltsu", "っ"}, {"xyo", "ょ"}, {"xyu", "ゅ"}, {"xya", "ゃ"},
                {"lya", "ゃ"}, {"lyu", "ゅ"}, {"lyo", "ょ"},

                {"si", "し"}, {"sya", "しゃ"}, {"syu", "しゅ"}, {"syo", "しょ"},
                {"ti", "ち"}, {"tya", "ちゃ"}, {"tyu", "ちゅ"}, {"tyo", "ちょ"},
                {"tu", "つ"},
                {"hu", "ふ"},
                {"zi", "じ"}, {"zya", "じゃ"}, {"zyu", "じゅ"}, {"zyo", "じょ"},
                {"di", "ぢ"}, {"du", "づ"},
                {"dya", "ぢゃ"}, {"dyu", "ぢゅ"}, {"dyo", "ぢょ"},

                {"ca", "か"}, {"ci", "し"}, {"cu", "く"}, {"ce", "せ"}, {"co", "こ"},
                {"qa", "くぁ"}, {"qi", "くぃ"}, {"qu", "く"}, {"qe", "くぇ"}, {"qo", "くぉ"},

                {"fa", "ふぁ"}, {"fi", "ふぃ"}, {"fe", "ふぇ"}, {"fo", "ふぉ"}, {"fyu", "ふゅ"},
                {"va", "ゔぁ"}, {"vi", "ゔぃ"}, {"vu", "ゔ"}, {"ve", "ゔぇ"}, {"vo", "ゔぉ"},
                {"vya", "ゔゃ"}, {"vyu", "ゔゅ"}, {"vyo", "ゔょ"},
                {"she", "しぇ"}, {"je", "じぇ"}, {"che", "ちぇ"},
                {"tsa", "つぁ"}, {"tsi", "つぃ"}, {"tse", "つぇ"}, {"tso", "つぉ"},
                {"thi", "てぃ"}, {"thu", "てゅ"}, {"the", "てぇ"}, {"tho", "てょ"},
                {"dhi", "でぃ"}, {"dhu", "でゅ"}, {"dhe", "でぇ"}, {"dho", "でょ"},
                {"twa", "とぁ"}, {"twi", "とぃ"}, {"twu", "とぅ"}, {"twe", "とぇ"}, {"two", "とぉ"},
                {"dwa", "どぁ"}, {"dwi", "どぃ"}, {"dwu", "どぅ"}, {"dwe", "どぇ"}, {"dwo", "どぉ"},
                {"kwa", "くぁ"}, {"kwi", "くぃ"}, {"kwe", "くぇ"}, {"kwo", "くぉ"},
                {"gwa", "ぐぁ"}, {"gwi", "ぐぃ"}, {"gwe", "ぐぇ"}, {"gwo", "ぐぉ"}
        };

        for (String[] x : data)
        {
            ROMAJI_TO_HIRAGANA.put(x[0], x[1]);
        }
    }

    public static String format(String funName, String s)
    {
        return switch (funName)
        {
            case "romaji-to-hiragana" -> romajiToHiragana(s);
            case "katakana-to-hiragana" -> katakanaToHiragana(s);
            case "romaji-to-katakana" -> hiraganaToKatakana(romajiToHiragana(s));
            case "hiragana-to-katakana" -> hiraganaToKatakana(s);
            default -> throw new IllegalArgumentException("不支持的日语转换：" + funName);
        };
    }

    public static String romajiToHiragana(String input)
    {
        String source = input.toLowerCase(Locale.ROOT);
        StringBuilder result = new StringBuilder();
        int i = 0;

        while (i < source.length())
        {
            char current = source.charAt(i);

            if (i + 1 < source.length() && isDoubleConsonant(source, i))
            {
                result.append("っ");
                i++;
                continue;
            }

            if (current == 'n')
            {
                if (i + 1 >= source.length())
                {
                    result.append("ん");
                    i++;
                    continue;
                }

                char next = source.charAt(i + 1);
                if (next == '\'')
                {
                    result.append("ん");
                    i += 2;
                    continue;
                }
                if (!isVowel(next) && next != 'y' && next != 'n')
                {
                    result.append("ん");
                    i++;
                    continue;
                }
            }

            boolean matched = false;
            for (int len = 5; len >= 1; len--)
            {
                if (i + len > source.length())
                    continue;

                String part = source.substring(i, i + len);
                String kana = ROMAJI_TO_HIRAGANA.get(part);
                if (kana != null)
                {
                    result.append(kana);
                    i += len;
                    matched = true;
                    break;
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

    public static String hiraganaToKatakana(String input)
    {
        StringBuilder result = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++)
        {
            char ch = input.charAt(i);
            if (ch >= 'ぁ' && ch <= 'ゖ')
            {
                result.append((char) (ch + ('ァ' - 'ぁ')));
            } else if (ch == 'ゔ')
            {
                result.append('ヴ');
            } else if (ch == 'ゝ')
            {
                result.append('ヽ');
            } else if (ch == 'ゞ')
            {
                result.append('ヾ');
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static String katakanaToHiragana(String input)
    {
        StringBuilder result = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++)
        {
            char ch = input.charAt(i);
            if (ch >= 'ァ' && ch <= 'ヶ')
            {
                result.append((char) (ch - ('ァ' - 'ぁ')));
            } else if (ch == 'ヴ')
            {
                result.append('ゔ');
            } else if (ch == 'ヽ')
            {
                result.append('ゝ');
            } else if (ch == 'ヾ')
            {
                result.append('ゞ');
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    private static boolean isDoubleConsonant(String source, int index)
    {
        char current = source.charAt(index);
        char next = source.charAt(index + 1);
        return current == next && isConsonant(current) && current != 'n';
    }

    private static boolean isVowel(char c)
    {
        return "aiueo".indexOf(c) >= 0;
    }

    private static boolean isConsonant(char c)
    {
        return c >= 'a' && c <= 'z' && !isVowel(c);
    }
}
