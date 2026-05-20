package com.shuowen.yuzong;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.StringsComparator;

import java.util.List;
import java.util.Scanner;

import static com.shuowen.yuzong.data.model.Word.CiyuTool.match;

public class Test
{



    static List<Triple<String, String, Boolean>> a = List.of(
            Triple.of("水", "水", true),
            Triple.of("水", "喝水", true),
            Triple.of("水", "水龍頭", true),
            Triple.of("水", "滔滔江水", true),

            Triple.of("中華人民共和國", "中華", true),
            Triple.of("中華人民共和國", "人民", true),
            Triple.of("中華人民共和國", "人類", false),
            Triple.of("中華人民共和國", "共和國", true),
            Triple.of("中華人民共和國", "國家", false),
            Triple.of("中華人民共和國", "中華人民", true),
            Triple.of("中華人民共和國", "中華民國", false),
            Triple.of("中華人民共和國", "中華民族", false),

            Triple.of("西瓜", "西", true),
            Triple.of("西瓜", "瓜", true),
            Triple.of("西瓜", "冬瓜", false),
            Triple.of("西瓜", "西瓜子", true),
            Triple.of("西瓜子", "西瓜", true),
            Triple.of("西瓜子", "西瓜瓤", true)
    );



    public static void main(String[] args)
    {
        for (var i : a)
        {
            if (match(i.getLeft(), i.getMiddle()) != i.getRight())
                System.out.println(i + " " + match(i.getLeft(), i.getMiddle()));
        }

//        Scanner sc = new Scanner(System.in);
//        String a = sc.nextLine();
//        String b = sc.nextLine();
//
//        System.out.println(lcs(a, b).length() * 1.0 / a.length());
//        System.out.println(lcs(a, b).length() * 1.0 / b.length());
//
//        main(args);

    }
}
