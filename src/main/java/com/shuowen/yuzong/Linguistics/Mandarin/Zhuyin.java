package com.shuowen.yuzong.Linguistics.Mandarin;

import lombok.Data;

@Data
public class Zhuyin
{
    String Sheng = "", Jie = "", Yun = "";
    int tone;
    boolean valid=true;

    /**
     * T：臺灣注音符號側標（T為Taiwan的首字母）
     */
    public char[] sidemarkT = {' ', ' ', 'ˊ', 'ˇ', 'ˋ', '·'};

    @Override
    public String toString()
    {
        return Sheng + Jie + Yun + tone;
    }

    public String toString(boolean a)
    {
        return Sheng + Jie + Yun;
    }

    public Zhuyin(String pinyin)
    {
        toZhuYin(pinyin);
    }

    private void toZhuYin(String pinyin)
    {
        if (pinyin.equals("none5"))
        {
            valid=false;
            return;
        }

        StringBuilder str = new StringBuilder(pinyin);

        //聲調是最後一個字母，讀取并刪除
        tone = str.charAt(str.length() - 1) - '0';
        str.deleteCharAt(str.length() - 1);

        //兒化音不和其他讀音拼合
        if (str.toString().equals("er"))
        {
            Yun = "ㄦ";
            return;
        }

        int i = 0;

        Sheng = switch (str.charAt(i))
        {
            case 'b' -> "ㄅ";
            case 'p' -> "ㄆ";
            case 'm' -> "ㄇ";
            case 'f' -> "ㄈ";
            case 'd' -> "ㄉ";
            case 't' -> "ㄊ";
            case 'n' -> "ㄋ";
            case 'l' -> "ㄌ";
            case 'g' -> "ㄍ";
            case 'k' -> "ㄎ";
            case 'h' -> "ㄏ";
            case 'j' ->
            {
                if (str.charAt(i + 1) == 'u') str.setCharAt(i + 1, 'v');
                yield "ㄐ";
            }
            case 'q' ->
            {
                if (str.charAt(i + 1) == 'u') str.setCharAt(i + 1, 'v');
                yield "ㄑ";
            }
            case 'x' ->
            {
                if (str.charAt(i + 1) == 'u') str.setCharAt(i + 1, 'v');
                yield "ㄒ";
            }
            case 'r' ->
            {
                if (str.charAt(i + 1) == 'i') i++;
                yield "ㄖ";
            }
            case 'z' ->
            {
                String res;
                if (str.charAt(i + 1) == 'h')
                {
                    i++; res = "ㄓ";
                }
                else res = "ㄗ";
                if (str.charAt(i + 1) == 'i') i++;
                yield res;
            }
            case 'c' ->
            {
                String res;
                if (str.charAt(i + 1) == 'h')
                {
                    i++; res = "ㄔ";
                }
                else res = "ㄘ";
                if (str.charAt(i + 1) == 'i') i++;
                yield res;
            }
            case 's' ->
            {
                String res;
                if (str.charAt(i + 1) == 'h')
                {
                    i++; res = "ㄕ";
                }
                else res = "ㄙ";
                if (str.charAt(i + 1) == 'i') i++;
                yield res;
            }
            case 'y' ->
            {
                if (str.charAt(i + 1) == 'i')
                {
                }//ying
                else if (str.charAt(i + 1) == 'u')
                {
                    str.setCharAt(i + 1, 'v');
                }//yuan->yvan
                else
                {
                    str.setCharAt(i, 'i'); i--;
                }  //yan->ian
                yield "";
            }
            case 'w' ->
            {
                if (str.charAt(i + 1) == 'u')
                {
                }  //wu
                else
                {
                    str.setCharAt(i, 'u'); i--;
                } //wan->uan
                yield "";
            }
            default ->
            {
                i--;
                yield "";
            }
        };
        i++;
        if (i == str.length()) return;


        if (str.toString().endsWith("ong"))
        {
            Jie = (str.toString().endsWith("iong")) ? "ㄩ" : "ㄨ";
            Yun = "ㄥ";
            return;
        }


        Jie = switch (str.charAt(i))
        {
            case 'i' -> "ㄧ";
            case 'u' -> "ㄨ";
            case 'v' -> "ㄩ";
            default ->
            {
                i--; yield "";
            }
        };
        i++;

        //祇有介韻母沒有
        if (i == str.length()) return;

        //如果有介韻母
        if (!Jie.isEmpty())
        {
            Yun += switch (str.charAt(i))
            {
                //jie jue
                case 'e' ->
                {
                    if (Jie.equals("ㄧ") || Jie.equals("ㄩ"))
                    {
                        yield "ㄝ";
                    }
                    i--;
                    yield "";
                }
                //jin jing
                case 'n' ->
                {
                    if (str.toString().endsWith("ng"))
                    {
                        i++;
                        yield "ㄥ";
                    }
                    else yield "ㄣ";//in un vn
                }
                case 'u' -> "ㄡ";
                case 'i' -> "ㄟ";
                default ->
                {
                    i--; yield "";
                }
            };
            i++;
        }
        if (i == str.length()) return;

        Yun += switch (str.charAt(i))
        {
            case 'a' ->
            {
                if (i + 1 == str.length()) yield "ㄚ";
                if (i + 2 == str.length())
                {
                    if (str.charAt(i + 1) == 'i') yield "ㄞ";
                    if (str.charAt(i + 1) == 'o') yield "ㄠ";
                    if (str.charAt(i + 1) == 'n') yield "ㄢ";
                }
                if (i + 3 == str.length()) yield "ㄤ";
                yield "";
            }
            case 'o' ->
            {
                if (i + 1 == str.length()) yield "ㄛ";
                if (i + 2 == str.length()) yield "ㄡ";
                yield "";
            }
            case 'e' ->
            {
                if (i + 1 == str.length()) yield "ㄜ";
                if (i + 2 == str.length())
                {
                    if (str.charAt(i + 1) == 'i') yield "ㄟ";
                    if (str.charAt(i + 1) == 'n') yield "ㄣ";
                }
                if (i + 3 == str.length()) yield "ㄥ";
                yield "";
            }
            default -> "";
        };
    }
}
