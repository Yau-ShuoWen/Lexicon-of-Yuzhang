package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.Linguistics.Format.CuanStyle;
import com.shuowen.yuzong.Linguistics.Format.StyleParams;

/**
 * 四川话拼音
 *
 * @author 姚说文
 */

public class CuanPinyin extends UniPinyin
{
    static char[] mark = {' ', '̄', '̣', '̀', '̌'};

    public CuanPinyin(String s)
    {
        super(s);//按照通用格式格式化
    }

    public CuanPinyin(String s, boolean v)
    {
        super(s, v);
    }

    @Override
    protected boolean isToneValid(int n)
    {
        return n >= 0 && n < mark.length;
    }

    @Override
    protected void scan()
    {
        pinyin = pinyin
                //汉语拼音代声母
                .replace("yi", "i")
                .replace("wu", "u")
                .replace("yu", "v")
                .replace("w", "u")
                .replace("y", "i")
                //ao
                .replace("ao", "au");
    }


    @Override
    public String toString()
    {
        //默认配置
        return toString(new CuanStyle());
    }

    public String toString(StyleParams params)
    {
        if (isInvalid()) return INVALID_PINYIN;
        show = pinyin;


        CuanStyle p = (params instanceof CuanStyle)
                ? (CuanStyle) params : new CuanStyle();

        addMark(p.num);
        setFormat(p.yu, p.gn, p.tz, p.alt, p.capital);
        return " //" + show + "// ";
    }


    public void setFormat(int yu, int gn, int tz, int alt, int capital)
    {
        String s = show;

        if (gn > 0)
        {
            s = s.replace("ni", "gni");
            s = s.replace("nv", "gnv");
        }
        if (yu > 0)
        {
            if (yu == 1) s = s.replace("v", "ü");
            if (yu == 2) s = s.replace("v", "yu");
        }
        if (tz > 0)
        {
            if (tz == 1) s = s.replace("tz", "r");
            if (tz == 2) s = s.replace("tz", "l");
        }
        if (alt > 0)
        {
            char c = s.charAt(0);
            if (alt == 1)//符合普通话规律
            {
                if (c == 'i')
                {
                    // i->y  ia ie iei iau ieu ien iang
                    // i->yi i in
                    if (s.equals("i") || s.equals("in"))
                        s = "y" + s;
                    else s = "y" + s.substring(1);
                }
                if (c == 'u')
                {
                    // u->w  ua uai uei uan uen uang
                    // u->wu u ue

                    if (s.equals("u") || s.equals("ue"))
                        s = "w" + s;
                    else s = "w" + s.substring(1);
                }
                if (c == 'v' || c == 'ü')
                {
                    s = s.replace("v", "yu");
                    s = s.replace("ü", "yu");//转到yu处理逻辑
                }
            }
            if (alt == 2)//硬加
            {
                if (c == 'i' || c == 'v') s = "y" + s;
                if (c == 'u') s = "w" + s;
            }
        }
        if (capital > 0)
        {
            if (capital == 1) s = s.toUpperCase();
            if (capital == 2) s = s.substring(0, 1).toUpperCase() + s.substring(1);
        }
        show = s;
    }

    @Override
    public void toCode()
    {
        String s = pinyin;
        StringBuilder Str = new StringBuilder(s);

        int S = 0, J = 0, Y = 0, W = 0;
        int l = 0, r = s.length();
        String Sub;

        //声母

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
                if (Str.length() > 1 && Str.charAt(1) == 'z')
                {
                    S += 19; l++;
                }
                else
                {
                    S += 6;
                }
                break;
            case 'n':
                if (Str.length() > 1 && Str.charAt(1) == 'g')//ng
                {
                    S += 10; l++;
                }
                else if (Str.length() > 1 && (Str.charAt(1) == 'i' || Str.charAt(1) == 'v'))//gn
                {
                    S += 14;
                }
                else//n
                {
                    S += 7;
                }

                break;
            case 'g':
                S += 8; break;
            case 'k':
                S += 9; break;
            case 'h':
                S += 11; break;
            case 'j':
                S += 12; break;
            case 'q':
                S += 13; break;
            case 'x':
                S += 15; break;
            case 'z':
                S += 16; break;
            case 'c':
                S += 17; break;
            case 's':
                S += 18; break;
            default:
                l--;
                break;
        }
        l++;

        Sub = Str.substring(l, r);
        l = 0; r = Sub.length();

        String answer = (S < 10) ? ("0" + S) : ("" + S);

        if (S >= 16 && Sub.equals("i"))//zi ci si tzi
        {
            code = answer + "100";
            return;
        }
        if (Sub.isEmpty())
        {
            code = answer + "000";
            return;
        }
        if (Sub.equals("er"))//er以及所有儿化音
        {
            code = answer + "200";
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
                    W += 3;
                    break;
                case 'u':
                    W += 4;
                    break;
                case 'n':
                    W += 5;
                    break;
                case 'g':
                    W += 6; r--;
                    break;
                default:
                    r++;
                    break;
            }
            r--;
            Sub = Sub.substring(l, r);
        }

        if (Sub.isEmpty())
        {
            if (W == 5)//介母代韵 且韵尾为n in un vn ->ien uen ven
            {
                Y += 3;
            }
        }
        else
        {
            switch (Sub)
            {
                case "a":
                    Y += 1; break;
                case "o":
                    Y += 2; break;
                case "e":
                    Y += 3; break;
            }
        }

        code = answer + W + J + Y;
        return;
    }


    public void addMark(int num)
    {
        if (tone == 0) return;

        StringBuilder Str = new StringBuilder(show);

        int idx = -1;
        int i = Str.length();
        while (i-- > 0)
        {
            char c = Str.charAt(i);
            if (c == 'a' || c == 'o' || c == 'e')
            {
                idx = i; break;
            }
            //TODO: 待办：是否需要iu并排标载后？ 回复：暂时不管
            if (c == 'i' || c == 'u' || c == 'v')
            {
                idx = i;
            }
        }
        if (idx == -1) Str.append(mark[tone]);
        else Str.insert(idx + 1, mark[tone]);

        show = Str.toString();
    }
}
