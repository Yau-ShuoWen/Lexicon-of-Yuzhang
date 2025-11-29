package com.shuowen.yuzong.Linguistics.Mandarin;


import lombok.Getter;

/**
 * 这是一个不可变格式
 */
@Getter
public class Zhuyin
{
    String Sheng = "", Jie = "", Yun = "";
    int tone;
    boolean valid = true;

    /**
     * T：臺灣注音符號側標（T為Taiwan的首字母）
     */
    public char[] sidemark = {' ', ' ', 'ˊ', 'ˇ', 'ˋ'};

    /**
     * @apiNote 为了宽泛适配，第0调和第5调都被认为是轻声
     */
    @Override
    public String toString()
    {
        if (!valid) return "[无效]";
        else return (tone > 1 && tone < 4) ?
                Sheng + Jie + Yun + sidemark[tone] :
                "·" + Sheng + Jie + Yun;
    }


    public String toStringWithNumTone()
    {
        if (!valid) return "[无效]";
        else return Sheng + Jie + Yun + tone;
    }

    public String toStringWithoutTone()
    {
        if (!valid) return "[无效]";
        else return Sheng + Jie + Yun;
    }

    public Zhuyin(String pinyin)
    {
        toZhuYin(pinyin, true);
    }

    public Zhuyin(String pinyin, boolean useTone)
    {
        toZhuYin(pinyin, useTone);
    }

    public static Zhuyin of(String pinyin)
    {
        return new Zhuyin(pinyin);
    }

    public static Zhuyin of(String pinyin, boolean useTone)
    {
        return new Zhuyin(pinyin, useTone);
    }

    private void toZhuYin(String pinyin, boolean useTone)
    {
        if (pinyin.equals("none5"))
        {
            valid = false;
            return;
        }

        StringBuilder str = new StringBuilder(pinyin);

        if (useTone)
        {
            //聲調是最後一個字母，讀取并刪除
            tone = str.charAt(str.length() - 1) - '0';
            str.deleteCharAt(str.length() - 1);
        }
        else
        {
            tone = 5;
        }

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

    /**
     * 把注音转换为更容易解析度Code代码，便于复杂方案的解析
     *
     * @return 长度为五位的数字
     */
    public String toCode()
    {
        return switch (Sheng)
        {
            case "ㄅ" -> "01";
            case "ㄆ" -> "02";
            case "ㄇ" -> "03";
            case "ㄈ" -> "04";
            case "ㄉ" -> "05";
            case "ㄊ" -> "06";
            case "ㄋ" -> "07";
            case "ㄌ" -> "08";
            case "ㄍ" -> "09";
            case "ㄎ" -> "10";
            case "ㄏ" -> "11";
            case "ㄐ" -> "12";
            case "ㄑ" -> "13";
            case "ㄒ" -> "14";
            case "ㄓ" -> "15";
            case "ㄔ" -> "16";
            case "ㄕ" -> "17";
            case "ㄖ" -> "18";
            case "ㄗ" -> "19";
            case "ㄘ" -> "20";
            case "ㄙ" -> "21";
            default -> "00";
        } + switch (Jie)
        {
            case "ㄧ" -> "1";
            case "ㄨ" -> "2";
            case "ㄩ" -> "3";
            default -> "0";
        } + switch (Yun)
        {
            case "ㄚ" -> "01";
            case "ㄛ" -> "02";
            case "ㄜ" -> "03";
            case "ㄝ" -> "04";
            case "ㄞ" -> "05";
            case "ㄟ" -> "06";
            case "ㄠ" -> "07";
            case "ㄡ" -> "08";
            case "ㄢ" -> "09";
            case "ㄣ" -> "10";
            case "ㄤ" -> "11";
            case "ㄥ" -> "12";
            case "ㄦ" -> "13";
            default -> "0";
        };
    }

    /**
     * 在上面函数的基础上再加上音调符号
     */
    public String toCodeWithTone()
    {
        return toCode() + tone;
    }

    /**
     * 这里转换的内容是中国大陆的，软件里会有的，可供输入的内容
     * <ul>
     *     <li>软件里会有的：只存在字典里的读音ê m等，输入法会用ei en等代替，不考虑这个的转换</li>
     *     <li>可供输入的内容：使用的是键盘的版本，没有ü的，要么变成u，要么变成v</li>
     * </ul>
     *
     * @see HanPinyin HanPinyin：完整的汉语拼音处理类
     */
    public String toPinyin()
    {
        boolean zero = false;  //零声母
        String sheng = switch (Sheng)
        {
            case "ㄅ" -> "b";
            case "ㄆ" -> "p";
            case "ㄇ" -> "m";
            case "ㄈ" -> "f";
            case "ㄉ" -> "d";
            case "ㄊ" -> "t";
            case "ㄋ" -> "n";
            case "ㄌ" -> "l";
            case "ㄍ" -> "g";
            case "ㄎ" -> "k";
            case "ㄏ" -> "h";
            case "ㄐ" -> "j";
            case "ㄑ" -> "q";
            case "ㄒ" -> "x";
            case "ㄓ" -> "zh";
            case "ㄔ" -> "ch";
            case "ㄕ" -> "sh";
            case "ㄖ" -> "r";
            case "ㄗ" -> "z";
            case "ㄘ" -> "c";
            case "ㄙ" -> "s";
            default ->
            {
                zero = true;
                yield "";
            }
        };

        String yun = switch (Jie + Yun)
        {
            case "ㄚ" -> "a";
            case "ㄛ" -> "o";
            case "ㄜ" -> "e";
            case "ㄞ" -> "ai";
            case "ㄟ" -> "ei";
            case "ㄠ" -> "ao";
            case "ㄡ" -> "ou";
            case "ㄢ" -> "an";
            case "ㄣ" -> "en";
            case "ㄤ" -> "ang";
            case "ㄥ" -> "eng";
            case "ㄦ" -> "er";

            case "ㄧ" -> (zero) ? "yi" : "i";
            case "ㄧㄚ" -> (zero) ? "ya" : "ia";
            case "ㄧㄛ" -> (zero) ? "yo" : "io";
            case "ㄧㄝ" -> (zero) ? "ye" : "ie";
            case "ㄧㄠ" -> (zero) ? "yao" : "iao";
            case "ㄧㄡ" -> (zero) ? "you" : "iu";
            case "ㄧㄢ" -> (zero) ? "yan" : "ian";
            case "ㄧㄣ" -> (zero) ? "yin" : "in";
            case "ㄧㄤ" -> (zero) ? "yang" : "iang";
            case "ㄧㄥ" -> (zero) ? "ying" : "ing";

            case "ㄨ" -> (zero) ? "wu" : "u";
            case "ㄨㄚ" -> (zero) ? "wa" : "ua";
            case "ㄨㄛ" -> (zero) ? "wo" : "uo";
            case "ㄨㄞ" -> (zero) ? "wai" : "uai";
            case "ㄨㄟ" -> (zero) ? "wei" : "ui";
            case "ㄨㄢ" -> (zero) ? "wan" : "uan";
            case "ㄨㄣ" -> (zero) ? "wen" : "un";
            case "ㄨㄤ" -> (zero) ? "wang" : "uang";
            case "ㄨㄥ" -> (zero) ? "weng" : "ong";

            case "ㄩ" -> (zero) ? "yu" : "ü";
            case "ㄩㄝ" -> (zero) ? "yue" : "üe";
            case "ㄩㄢ" -> (zero) ? "yuan" : "üan";
            case "ㄩㄣ" -> (zero) ? "yun" : "ün";
            case "ㄩㄥ" -> (zero) ? "yong" : "iong";

            case "" -> "i";//如果没有任何声母，那说明是zhi chi shi ri zi ci si
            default -> "";
        };
        String ans = (sheng + yun);
        return ans.replace("ü", (ans.matches("[jqx]ü[a-z]*")) ? "u" : "v") + tone;
    }
}
