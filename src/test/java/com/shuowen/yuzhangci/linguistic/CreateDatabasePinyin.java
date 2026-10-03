package com.shuowen.yuzhangci.linguistic;

import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;

import java.util.Scanner;

public class CreateDatabasePinyin
{
    static Dialect d = Dialect.LAC;

    public static void main(String[] args)
    {
        while (true)
        {
            Scanner sc = new Scanner(System.in);
            String pyText = sc.nextLine();
            if (pyText.equals("exit")) return;

            if (!pyText.trim().isEmpty())
            {
                String ans = "";
                for (var i : pyText.trim().split(" "))
                {
                    ans += d.tryCreatePinyin(KeyboardPinyin.of(i)).handleIfExist(py -> py.toDatabasePinyin().toString(true))
                            .getValueOrDefault("×")
                            + " ";
                }
                System.out.println(ans.trim());
            }
        }
    }
}
