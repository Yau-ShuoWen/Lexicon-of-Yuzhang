package com.shuowen.yuzong;

import java.text.Normalizer;
import java.util.Scanner;


public class TestInteraction
{
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        while (true)
        {
            String input = sc.nextLine();
            if("-".equals(input)) return;

            String 啊=Normalizer.normalize(input, Normalizer.Form.NFD);

                    for(var i:啊.toCharArray())
                    {
                        System.out.print(i+" ");
                    }
                    System.out.println();
           // System.out.println(Normalizer.normalize(input, Normalizer.Form.NFD));;

        }
    }
}
