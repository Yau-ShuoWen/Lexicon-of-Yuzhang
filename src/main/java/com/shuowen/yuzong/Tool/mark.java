package com.shuowen.yuzong.Tool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.text.Normalizer;
import java.util.Scanner;

public class mark
{
    public static String filter(String str)
    {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        return str.replace("ü","ü");
    }

    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        while (true)
        {
            String str = sc.nextLine();
            str = Normalizer.normalize(str, Normalizer.Form.NFD);
            System.out.println(filter(str));
//            for (int i = 0; i < str.length(); i++)
//            {
//                System.out.println("a" + str.charAt(i));
//            }
        }
    }
}
