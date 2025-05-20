package com.shuowen.yuzong.Linguistics.Mandarin;


import com.hankcs.hanlp.*;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.hankcs.hanlp.dictionary.py.PinyinDictionary;

import java.util.*;

/**
 * 所有汉语拼音有关内容都放在这里， Pinyin类不要用在外面，外面直接用字符串接住
 * */

public class HanPinyin
{
    /** H:漢語拼音側標 */
    public char[] sidemarkH = {' ', 'ˉ', 'ˊ', 'ˇ', 'ˋ', ' '};

    /**T：臺灣注音符號側標（T為Taiwan的首字母）*/
    public char[] sidemarkT = {' ', ' ', 'ˊ', 'ˇ', 'ˋ', '·'};

    /**
     *字转拼音数组：一段这样的文字，自動判斷多音字 -> [yi1, duan4, zhe4, yang4, de5, wen2, zi4]
     *  */
    public List<String> txt_Pinyin(String txt)
    {
        List<Pinyin> a= HanLP.convertToPinyinList(txt);
        List<String> ans=new ArrayList<>();
        for (Pinyin p : a)
        {
            ans.add(p.toString());
        }
        if(ans.size()!=txt.length()) throw new RuntimeException("char num cant match");
        return ans;
    }

    /**
     * 字转拼音数组：行 ->[xing2, hang2]
     * */
    public List<String> toPinyin(String c)//看上去是字符串，但只调用一个字
    {
        Pinyin[] ans = PinyinDictionary.get(c);
        List<String> py=new ArrayList<>();
        if(ans==null)
        {
            py.add("none5");
            return py;
        }
        for(Pinyin i:ans)
        {
            py.add(i.toString());
        }
        return py;
    }

    /**
     * 拼音去除标号  zhe4->zhe
     * 沒有合法性檢查
     * */
    public String toPinyin_(String string1)
    {
        if(string1.equals("none5")) return null;

        StringBuilder str = new StringBuilder(string1);
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    /**
     * 拼音轉注音標號  zhe4->4
     * 沒有合法性檢查
     * */
    public int toZYtongue(String str)
    {
        if (str.equals("none5")) return 0;

        if (str.isEmpty()) throw new RuntimeException("空字符串");
        if (str.length() == 1) throw new RuntimeException("無效字符串");

        char ans=str.charAt(str.length() - 1);
        return (int)ans-'0';
    }

    /**
     * 一个电脑处理的拼音变常见拼音，把pin1yin1转换成pīnyīn
     *
     **/
    public String topmark(String string1)
    {
        int tongue=toZYtongue(string1);
        if(tongue==0) return null;
        StringBuilder str = new StringBuilder(string1);
        str.deleteCharAt(str.length() - 1);

        for(int i=0;i<str.length();i++) if(str.charAt(i)=='v') str.setCharAt(i,'ü');
        for(int i=0;i<str.length();i++) if(str.charAt(i)=='a')
        {
            char[] s = {' ','ā','á','ǎ','à','a'};
            str.setCharAt(i,s[tongue]);
            return str.toString();
        }
        for(int i=0;i<str.length();i++) if(str.charAt(i)=='o')
        {
            char[] s = {' ','ō','ó','ǒ','ò','o'};
            str.setCharAt(i,s[tongue]);
            return str.toString();
        }
        for(int i=0;i<str.length();i++) if(str.charAt(i)=='e')
        {
            char[] s = {' ','ē','é','ě','è','e'};
            str.setCharAt(i,s[tongue]);
            return str.toString();
        }
        for(int i=0;i<str.length();i++) if(str.charAt(i)=='i')
        {
            if(i==str.length()-1||str.charAt(str.length()-1)!='u')
            {

            }
            char[] s = {' ','ī','í','ǐ','ì','i'};
            str.setCharAt(i,s[tongue]);
            return str.toString();
        }
        for(int i=0;i<str.length();i++) if(str.charAt(i)=='u')
        {
            char[] s = {' ','ū','ú','ǔ','ù','u'};
            str.setCharAt(i,s[tongue]);
            return str.toString();
        }
        for(int i=0;i<str.length();i++) if(str.charAt(i)=='u')
        {
            char[] s = {' ','ǖ','ǘ','ǚ','ǜ','ü'};
            str.setCharAt(i,s[tongue]);
            return str.toString();
        }
        char[] s = {' ','ˉ','ˊ','ˇ','ˋ',' '};
        str.append(s[tongue]);
        return str.toString();
    }

    /**
     * 拼音轉注音（不帶標號）  zhe4->ㄓㄜ
     * 沒有合法性檢查
     * */
    public String toZhuYin(String string1)
    {
        if(string1.equals("none5")) return null;

        StringBuilder str = new StringBuilder(string1);
        StringBuilder s = new StringBuilder();

        if(str.length()==0) throw new RuntimeException("空字符串");
        if(str.length()==1) throw new RuntimeException("無效字符串");

        //聲調是最後一個字母，讀取并刪除
        char tongue = str.charAt(str.length() - 1);
        str.deleteCharAt(str.length() - 1);

        //兒化音不和其他讀音拼合
        if(str.length()==2&&str.charAt(0)=='e'&&str.charAt(1)=='r')
        {
            s.append('ㄦ');
            return s.toString();
        }

        int i = 0;

        //處理聲母和代聲母
        switch (str.charAt(i))
        {
            case 'b': s.append('ㄅ');   break;
            case 'p': s.append('ㄆ');   break;
            case 'm': s.append('ㄇ');   break;
            case 'f': s.append('ㄈ');   break;
            case 'd': s.append('ㄉ');   break;
            case 't': s.append('ㄊ');   break;
            case 'n': s.append('ㄋ');   break;
            case 'l': s.append('ㄌ');   break;
            case 'g': s.append('ㄍ');   break;
            case 'k': s.append('ㄎ');   break;
            case 'h': s.append('ㄏ');   break;
            case 'j':
                s.append('ㄐ');
                if(str.charAt(i+1)=='u') str.setCharAt(i+1,'v');
                break;
            case 'q':
                s.append('ㄑ');
                if(str.charAt(i+1)=='u') str.setCharAt(i+1,'v');
                break;
            case 'x':
                s.append('ㄒ');
                if(str.charAt(i+1)=='u') str.setCharAt(i+1,'v');
                break;
            case 'r': s.append('ㄖ');
                if(str.charAt(i+1)=='i') return s.toString();
                break;
            case 'z':
                if(str.charAt(i+1)=='h') { s.append('ㄓ');i++; }
                else s.append('ㄗ');
                if(str.charAt(i+1)=='i') return s.toString();
                break;
            case 'c':
                if(str.charAt(i+1)=='h') { s.append('ㄔ');i++; }
                else s.append('ㄘ');
                if(str.charAt(i+1)=='i') return s.toString();
                break;
            case 's':
                if(str.charAt(i+1)=='h') { s.append('ㄕ');i++; }
                else s.append('ㄙ');
                if(str.charAt(i+1)=='i') return s.toString();
                break;
            case 'y':
                if(str.charAt(i+1)=='i') {}//ying
                else if(str.charAt(i+1)=='u') { str.setCharAt(i+1,'v');}//yuan->yvan
                else {str.setCharAt(i,'i');i--;}  //yan->ian
                break;
            case 'w':
                if(str.charAt(i+1)=='u') {}  //wu
                else {str.setCharAt(i,'u');i--;} //wan->uan
                break;
            case 'a':
            case 'o':
            case 'e':i--;
            default:
        }


        i++;
        boolean jiemu=true;
        char jiemu_ = 0;
        //處理介韻母
        switch (str.charAt(i))
        {
            case 'i':
                s.append('ㄧ');i++;
                jiemu_='i';
                break;
            case 'u':
                s.append('ㄨ');i++;
                jiemu_='u';
                break;
            case 'v':
                s.append('ㄩ');i++;
                jiemu_='v';
                break;
            default:jiemu=false;
        }

        //祇有介韻母沒有
        if(i==str.length()) return s.toString();

        //如果有介韻母
        if(jiemu)
        {
            switch (str.charAt(i))//處理複韻母不符合 介+韻 情況
            {
                case 'e':
                    if(jiemu_=='i'||jiemu_=='v')
                    {
                        s.append('ㄝ');// ie ve
                        return s.toString();
                    }
                    break;
                case 'n':
                    if(i<str.length()-1&&str.charAt(i+1)=='g')  //ing
                        s.append('ㄥ');
                    else s.append('ㄣ'); //in un yn
                    return s.toString();
                case 'u':
                    s.append('ㄡ');    //iu
                    return s.toString();
                case 'i':
                    s.append('ㄟ');    //ui
                    return s.toString();
                case 'o':
                    if(i+3==str.length())
                    {
                        s.deleteCharAt(s.length()-1);
                        s.append("ㄩㄥ");
                        return s.toString();
                    }
                default:
            }
        }

        //兩個奇葩轉意ong->ung 和 iong->vng
        if(i<str.length()-2&&str.charAt(i)=='o'&&str.charAt(i+1)=='n'&&str.charAt(i+2)=='g')
        {
            s.append("ㄨㄥ");
            return s.toString();
        }

        switch (str.charAt(i))
        {
            case 'a':
                if(i+1==str.length())  s.append('ㄚ');
                if(i+2==str.length())
                {
                    if(str.charAt(i+1)=='i')  s.append('ㄞ');
                    if(str.charAt(i+1)=='o')  s.append('ㄠ');
                    if(str.charAt(i+1)=='n')  s.append('ㄢ');
                }
                if(i+3==str.length())  s.append('ㄤ');
                break;
            case 'o':
                if(i+1==str.length())  s.append('ㄛ');
                if(i+2==str.length())  s.append('ㄡ');
                break;
            case 'e':
                if(i+1==str.length())  s.append('ㄜ');
                if(i+2==str.length())
                {
                    if(str.charAt(i+1)=='i')  s.append('ㄟ');
                    if(str.charAt(i+1)=='n')  s.append('ㄣ');
                }
                if(i+3==str.length())  s.append('ㄥ');
                break;
            default:
        }

        return s.toString();

        /*
         * 修改日誌
         * 1.第一次修改 漢han 的錯誤識別：忘記給開關語句加break
         * 2.第二次修改 国guo 的錯誤識別 gvng：uo 和uong 沒有判斷
         * 3.第三次修改 中zhong 在其中把檢查原字符串寫成了結果字符串，導致越界
         * 4.第四次修改 有you   iong的識別在国裡面uo，現在you->iou
         *            需xu   v去點
         * 5.第五次修改 用yong  iong的邏輯又錯了
         * 6.第六次修改 增加了none5拒絕操作的指令，過濾非漢字
         * 7.零声母：aoe开头有问题，并修改了er的过程
         * */
    }
}