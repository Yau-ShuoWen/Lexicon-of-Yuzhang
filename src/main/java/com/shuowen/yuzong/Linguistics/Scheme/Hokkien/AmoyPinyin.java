package com.shuowen.yuzong.Linguistics.Scheme.Hokkien;

import com.shuowen.yuzong.Linguistics.Format.StyleParams;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;

public class AmoyPinyin extends UniPinyin
{

    public AmoyPinyin(String s)
    {
        super(s);
    }

    public AmoyPinyin(String s, boolean num)
    {
        super(s, num);
    }

    @Override
    protected boolean isToneValid(int n)
    {
        return true;
    }

    @Override
    protected void scan()
    {

    }

    @Override
    public String toString(StyleParams params)
    {
        return "";
    }

    @Override
    protected void toCode()
    {
        String s = pinyin;

        if (s.length() <= 3)
        {
            String ans = switch (s)
            {
                case "m" -> "0000007";
                case "ng" -> "0000008";
                case "mq" -> "0010007";
                case "ngq" -> "0010008";
                default -> "";
            };
            if (!ans.isEmpty())
            {
                code = ans;
                return;
            }
        }


        StringBuilder Str = new StringBuilder(s);

        //声母 介母 韵母 鼻化 元音尾 辅音尾
        String S;
        int J = 0, Y = 0, B = 0, Yw = 0, Fw = 0;
        int l = 0, r = s.length();
        String Sub;

        // p ph b m  | d t n l | k kh g ng h | z c s
        S = switch (Str.charAt(0))
        {
            case 'b' -> "04";
            case 'p' ->
            {
                if (Str.length() > 1 && Str.charAt(1) == 'h')
                {
                    l++;
                    yield "02";
                }
                else yield "01";
            }
            case 'm' -> "03";
            case 'd' -> "05";
            case 't' -> "06";
            case 'n' ->
            {
                if (Str.length() > 1 && Str.charAt(1) == 'g')
                {
                    l++;
                    yield "12";
                }
                else yield "07";
            }
            case 'l' -> "08";
            case 'g' -> "11";
            case 'k' ->
            {
                if (Str.length() > 1 && Str.charAt(1) == 'h')
                {
                    l++;
                    yield "10";
                }
                else yield "09";
            }
            case 'h' -> "13";
            case 'z' -> "14";
            case 'c' -> "15";
            case 's' -> "16";
            default ->
            {
                l--;
                yield "00";
            }
        };
        l++;
        Sub = Str.substring(l, r);
        l = 0; r = Sub.length();

        if (Sub.length() <= 3)
        {
            String ans = switch (Sub)
            {
                case "m" -> "00007";
                case "ng" -> "00008";
                case "mq" -> "70007";
                case "ngq" -> "70008";
                default -> "";
            };
            if (!ans.isEmpty())
            {
                code = S + ans;
                return;
            }
        }

        // 识别介韵母并且删除
        if (!Sub.isEmpty())
        {
            J = switch (Sub.charAt(l))
            {
                case 'i' -> 1;
                case 'u' -> 2;
                default ->
                {
                    l--; yield 0;
                }
            };
            l++;
            Sub = Sub.substring(l, r);
            l = 0; r = Sub.length();

        }


        // 识别辅音韵尾并删除
        if (!Sub.isEmpty())
        {
            Fw = switch (Sub.charAt(r - 1))
            {
                case 'm' -> 1;
                case 'n' -> 2;
                case 'g' ->
                {
                    r--; yield 3;
                }
                case 'p' -> 4;
                case 't' -> 5;
                case 'k' -> 6;
                case 'q' -> 7;
                default ->
                {
                    r++; yield 0;
                }
            };
            r--;
            Sub = Sub.substring(l, r);
            l = 0; r = Sub.length();
        }

        // 识别元音尾并删除，如果带上鼻化，认为不是韵尾
        if (!Sub.isEmpty())
        {
            switch (Sub.charAt(Sub.length() - 1))
            {
                case 'i':
                    Yw += 1;
                    break;
                case 'u':
                    Yw += 2;
                    break;
                default:
                    r++;
                    break;
            }
            r--;
            Sub = Sub.substring(l, r);
            l = 0; r = Sub.length();
        }

        if (!Sub.isEmpty())
        {
            if (Sub.charAt(Sub.length() - 1) == 'x')
            {
                B = 1;
            }
            else
            {
                r++;
            }
            r--;
            Sub = Sub.substring(l, r);
        }

        if (!Sub.isEmpty())
        {
            Y = switch (Sub)
            {
                case "a" -> 1;
                case "o" -> 2;
                case "oo" -> 3;
                case "e" -> 4;
                case "i" -> 5;
                case "u" -> 6;
                default -> 0;
            };
        }
        code = S + Fw + B + Yw + J + Y;
    }
}


