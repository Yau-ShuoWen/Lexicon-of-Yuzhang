package com.shuowen.yuzong.Tool.format;

import com.shuowen.yuzong.Linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Quadruple;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.*;

public class CharToTable
{
    public static void main(String[] args)
    {
        try
        {
            String content = Files.readString(Path.of("GBK.txt"));
            FileWriter sqlWriter = new FileWriter("GBK.sql");

            //String[] list = content.split("\n");
            Set<String> list = new HashSet<>();
            for (int i = 0; i < content.length(); i++)
            {
                list.add(content.substring(i, i + 1));
            }
            List<Quadruple<String, String, String, Integer>> table = new ArrayList<>();
            for (var i : list)
            {
                try
                {
                    //System.out.println(i + " " + HanPinyin.toPinyin(i));
                    for (var j : HanPinyin.toPinyin(i))
                    {
                        table.add(new Quadruple<>(i,
                                HanPinyin.toSyllable(j) == null ? "-" : HanPinyin.toSyllable(j),
                                "-",//过时删除的用法：HanPinyin.toZhuYin(j) == null ? "-" : HanPinyin.toZhuYin(j),
                                HanPinyin.toTone(j)));
                    }
                } catch (Exception e)
                {
                    table.add(Quadruple.of("-", "-", "-", 0));
                }
            }

            table.sort(Comparator
                    .comparing((Quadruple<String, String, String, Integer> q) -> q.getGamma())
                    .thenComparing(q -> q.getDelta())
                    .thenComparing(q -> q.getAlpha()));

            for (var q : table)
            {
                //第一次新增這麼寫
                //sqlWriter.write("INSERT INTO NC.mdr_char (hanzi, pinyin, zhuyin, tone) VALUES ('" + q.getN1() + "','" + q.getN2() + "','" + q.getN3() + "'," + q.getN4() + ");\n");

                sqlWriter.write("INSERT IGNORE INTO NC.mdr_char (hanzi, pinyin, zhuyin, tone) VALUES ('" + q.getAlpha() + "','" + q.getBeta() + "','" + q.getGamma() + "'," + q.getDelta() + ");\n");
            }
//            System.out.println(table.size());

        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}
