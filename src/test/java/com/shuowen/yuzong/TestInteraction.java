package com.shuowen.yuzong;

import com.shuowen.yuzong.util.text.Translate;

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

           System.out.println(Translate.toSc(input));
        }
    }
}
