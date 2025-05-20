package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.JyutStyle;
import com.shuowen.yuzong.Linguistics.Format.StyleParams;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 粤语拼音方案
 *
 * @author 香港語言學會
 */
public class JyutPinyin extends UniPinyin
{
    int markSize;

    public JyutPinyin(String s)
    {
        super(s);
    }

    public JyutPinyin(String s, boolean b)
    {
        super(s, b);
    }

    @Override
    protected boolean isToneValid(int n)
    {
        //2025/04/29
        boolean range = (n >= 0 && n < markSize);

        boolean rhythm = true;

        if (pinyin == null || pinyin.isEmpty()) return false;

        char last = pinyin.charAt(pinyin.length() - 1);
        if (n >= 1 && n <= 6)
        {
            // 不是入声，但是结尾ptk
            if (last == 'p' || last == 't' || last == 'k') rhythm = false;
        }
        if (n >= 7 && n <= 9)
        {
            // 为入声，但是韵尾非ptk
            if (last != 'p' && last != 't' && last != 'k') rhythm = false;
        }
        return range && rhythm;
    }


    @Override
    protected void scan()
    {
        pinyin = pinyin.toLowerCase();

        final List<Pair<String, String>> rule = List.of(
                Pair.of("wu", "u"),
                Pair.of("w", "u"),
                Pair.of("jyu", "v"),
                Pair.of("yu", "v"),
                Pair.of("ji", "i"),
                Pair.of("j", "i"),
                Pair.of("yi", "i"),
                Pair.of("y", "i"),
                Pair.of("gw", "gu"),
                Pair.of("kw", "ku")
        );

        for (Pair<String, String> p : rule)
        {
            if (pinyin.startsWith(p.getLeft()))
            {
                pinyin = p.getRight() + pinyin.substring(p.getLeft().length());
                break;
            }
        }

        if (pinyin.endsWith("p") || pinyin.endsWith("t") || pinyin.endsWith("k"))
        {
            tone = switch (tone)
            {
                case 1 -> 7;
                case 3 -> 8;
                case 6 -> 9;
                default -> tone;
            };
        }
    }

    @Override
    public String toString()
    {
        return toString(new JyutStyle());
    }

    @Override
    public String toString(StyleParams params)
    {
        if (isInvalid()) return INVALID_PINYIN;
        show = pinyin;

        JyutStyle p = (params instanceof JyutStyle) ? (JyutStyle) params : new JyutStyle();

        addMark(p.num, p.plan);
        setFormat(p.plan, p.capital);
        return " //" + show + "// ";
    }

    /**
     * 将输入的拼音字符串根据指定的参数选项进行风格转换，用于处理方言拼音的展示或输出格式。
     *
     * @param plan 1 香港語言學會方案
     */
    public void setFormat(int plan, int capital)
    {
        String s = show;

        if (plan == 1)
        {
            if (s.charAt(0) == 'i')
            {
                if (s.length() == 1) s = "ji";
                else
                {
                    char c = s.charAt(1);
                    if (c == 'u' || c == 'm' || c == 'n' || c == 'p' || c == 't' || c == 'k')
                        s = 'j' + s;
                    else
                        s = 'j' + s.substring(1);
                }
            }
            if (s.charAt(0) == 'u')
            {
                // 祇有uk 和 ung 保留原型
                if (!(s.equals("uk") || s.equals("ung")))
                {
                    s = 'w' + s;
                }
            }
            if ((s.startsWith("gu") && s.length() > 2) || (s.startsWith("ku") && s.length() > 2))
            {
                s = "" + s.charAt(0) + 'w' + s.substring(2);
            }
            if (s.contains("v"))
            {
                if (s.charAt(0) == 'v')//vt->jyut
                {
                    s = s.replace("v", "jyu");
                }
                else //nvn->nyun
                {
                    s = s.replace("v", "yu");
                }
            }
        }
        if (plan == 2)
        {

        }

        if (capital > 0)
        {
            if (capital == 1) s = s.toUpperCase();
            if (capital == 2) s = s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        show = s;
    }

    // aa a a o oe eo o i
    // 0 i u n m ng p t k

    @Override
    protected void toCode()
    {
        String s = pinyin;
        StringBuilder Str = new StringBuilder(s);

        int S = 0, J = 0, Y = 0, W = 0;
        int l = 0, r = s.length();
        String Sub;

        // 聲母
        switch (Str.charAt(0))
        {
            case 'b':
                S += 1; break;
            case 'p':
                S += 2; break;
            case 'm':
                S += 3; break;
            case 'f':
                S += 4; break;
            case 'd':
                S += 5; break;
            case 't':
                S += 6; break;
            case 'n':
                if (Str.length() > 1 && Str.charAt(1) == 'g')
                {
                    S += 12; l++;
                }
                else
                {
                    S += 7;
                }
                break;
            case 'l':
                S += 8; break;
            case 'g':
                S += 9; break;
            case 'k':
                S += 11; break;
            case 'h':
                S += 13; break;
            case 'z':
                S += 14; break;
            case 'c':
                S += 15; break;
            case 's':
                S += 16; break;
            default:
                l--;
                break;
        }
        l++;

        Sub = Str.substring(l, r);
        l = 0; r = Sub.length();

        String answer = (S < 10) ? ("0" + S) : ("" + S);

        if (Sub.isEmpty())
        {
            code = answer + "000";
            return;
        }

        if (l < Str.length())
        {
            switch (Sub.charAt(l))
            {
                case 'i':
                    J += 1;
                    break;
                case 'u':
                    J += 2;
                    break;
                case 'v':
                    J += 3;
                    break;
                default:
                    l--;
                    break;
            }
            l++;
        }
        Sub = Sub.substring(l, r);
        l = 0; r = Sub.length();

        if (!Sub.isEmpty())
        {
            switch (Sub.charAt(Sub.length() - 1))
            {
                case 'i':
                    W += 1;
                    break;
                case 'u':
                    W += 2;
                    break;
                case 'n':
                    W += 3;
                    break;
                case 'm':
                    W += 4;
                    break;
                case 'g':
                    W += 5; r--;
                    break;
                case 'p':
                    W += 6;
                    break;
                case 't':
                    W += 7;
                    break;
                case 'k':
                    W += 8;
                    break;
                default:
                    r++;
                    break;
            }
            r--;
            Sub = Sub.substring(l, r);
        }


        if (!Sub.isEmpty())
        {
            switch (Sub)
            {
                case "aa":
                    Y += 1; break;
                case "a":
                    Y += 2; break;
                case "e":
                    Y += 3; break;
                case "oe":
                    Y += 4; break;
                case "eo":
                    Y += 5; break;
                case "o":
                    Y += 6; break;
                case "i":
                    Y += 7; break;
                case "u":
                    Y += 8; break;
            }
        }
        code = answer + W + J + Y;
    }


    /**
     * @param num 是否使用數字，收到方案影響，
     */
    protected void addMark(int num, int plan)
    {
        if (tone == 0) return;
        if (plan == 1)
        {
            tone = switch (tone)
            {
                case 7 -> 1;
                case 8 -> 2;
                case 9 -> 3;
                default -> tone;
            };
            show = pinyin + tone;
        }
    }
}
