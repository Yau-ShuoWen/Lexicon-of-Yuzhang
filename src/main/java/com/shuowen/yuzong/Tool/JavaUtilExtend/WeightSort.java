package com.shuowen.yuzong.Tool.JavaUtilExtend;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Quadruple;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.StringsComparator;

import java.util.*;

public class WeightSort
{
    private static String lcs(String a, String b)
    {
        StringsComparator comparator = new StringsComparator(a, b);
        StringBuilder lcs = new StringBuilder();

        comparator.getScript().visit(new CommandVisitor<>()
        {
            @Override
            public void visitInsertCommand(Character c)
            {
            }

            @Override
            public void visitKeepCommand(Character c)
            {
                lcs.append(c);
            }

            @Override
            public void visitDeleteCommand(Character c)
            {
            }
        });

        return lcs.toString();
    }

    private static double similarity(String a, String b)
    {
        if (a.isEmpty() && b.isEmpty()) return 1.0;
        return (2.0 * lcs(a, b).length()) / (a.length() + b.length());
    }

    private static double matching(String a, String b)
    {
        if (a.isEmpty() && b.isEmpty()) return 1.0;
        return (1.0 * lcs(a, b).length()) / b.length();
    }

    public static List<Quadruple<String, Double, Double, Double>>
    sort(List<String> list, String query, double a, double b)
    {
        List<Quadruple<String, Double, Double, Double>> l = new ArrayList<>();

        for (String s : list)
        {
            double similarity = similarity(s, query);
            double matching = matching(s, query);
            double weight = similarity * a + matching * b;
            l.add(new Quadruple<>(s, similarity, matching, weight));
        }

        l.sort((t1, t2) -> Double.compare(t2.getDelta(), t1.getDelta()));

        list.clear();
        for (var i : l) list.add(i.getAlpha());

        return l;
    }

    public static List<Quadruple<String, Double, Double, Double>>
    sort(List<String> list, String query)
    {
        return sort(list, query, 0.5, 0.5);
    }

    public static void main(String[] args)
    {
        while (true)
        {
            Scanner sc = new Scanner(System.in);

            List<String> set = new ArrayList<>();
            System.out.println("\n--输入字典---\n");
            while (true)
            {
                String s = sc.nextLine();
                if ("-".equals(s)) break;      // 输入 "-" 结束字典输入
                if ("--".equals(s)) return;    // 输入 "--" 结束测试程序
                set.add(s);
            }
            System.out.println("\n--输入查询文本--\n");
            String query = sc.nextLine();

            for (var i : WeightSort.sort(set, query))
            {
                System.out.println(i);
            }
        }
    }
}
