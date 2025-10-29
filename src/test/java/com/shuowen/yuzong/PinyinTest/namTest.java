package com.shuowen.yuzong.PinyinTest;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;

import java.util.Scanner;

public class namTest
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        NamStyle ns = new NamStyle();
        while (true)
        {
            String input = sc.next();
            if (input.startsWith("get"))
            {
                System.out.println(ns);
            }
            else if (input.startsWith("look"))
            {
                var a = NamPinyin.toPinyinList(input);
                for (var i : a) System.out.print(i.toString(ns).replace("/", ""));
            }
            else if (input.startsWith("exit")) return;
            else
            {
                int a;

                switch (input)
                {
                    case "yu", "gn", "oe", "ee", "ptk", "ii", "alt", "num", "cap" ->
                    {
                        a = sc.nextInt();
                    }
                    default ->
                    {
                        continue;
                    }
                }

                switch (input)
                {
//                    case "yu" -> ns.yu = a;
//                    case "gn" -> ns.gn = a;
//                    case "ee" -> ns.ee = a;
//                    case "oe" -> ns.oe = a;
//                    case "ii" -> ns.ii = a;
//                    case "ptk" -> ns.ptk = a;
//                    case "alt" -> ns.alt = a;
//                    case "num" -> ns.num = a;
//                    case "cap" -> ns.capital = a;
                }
                System.out.println(ns);
                System.out.println();
                System.out.println(
                        (NamPinyin.parseAndReplace("[fung1][qieu2][ia5][bok6]", ns) + "\n" +
                                NamPinyin.parseAndReplace("[nvot6][lok6][u1][ti2][song1][man3][tien1]", ns) + "\n" +
                                NamPinyin.parseAndReplace("[gong1][fung1][v4][fo3][dui4][ceu2][mien4]", ns) + "\n" +
                                NamPinyin.parseAndReplace("[gu1][su1][ceen2][uai5][hon2][san1][sii5]", ns) + "\n" +
                                NamPinyin.parseAndReplace("[ia5][pon5][zung1][seen1][tau5][kak6][cuon2]", ns))
                                .replace("/", "").replace("  ", " "));
            }
        }
    }
}
