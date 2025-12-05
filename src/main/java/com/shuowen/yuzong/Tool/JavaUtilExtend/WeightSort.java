package com.shuowen.yuzong.Tool.JavaUtilExtend;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import lombok.Data;
import org.apache.commons.text.diff.CommandVisitor;
import org.apache.commons.text.diff.StringsComparator;

import java.util.*;
import java.util.function.Function;

/**
 * 如果需要排序的信息，就使用他的返回值，如果不需要不必接收，也可以完成排序
 */
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

    @Data
    static class Info<T>
    {
        String keyword;
        double similarity; // 相似值
        double matching;   // 匹配值
        double priority;   // 优先值
        double weight;     // 综合权重
        T value;           // 内容

        public Info(T value, String query, String key, double priority,
                    double simCoef, double matchCoef, double priorCoef)
        {
            this.value = value;
            keyword = key;
            similarity = similarity(key, query);
            matching = matching(key, query);
            this.priority = priority;
            weight = similarity * simCoef + matching * matchCoef + priority * priorCoef;
        }
    }

    /**
     * 内部函数
     */
    private static <T> List<Info<T>> sort(
            List<T> list, List<Double> priority, Function<T, String> keyGetter, String query,
            double simCoef, double matchCoef, double priorCoef
    )
    {
        if (list.size() != priority.size()) throw new RuntimeException("数组长度不相等");
        List<Info<T>> infos = new ArrayList<>();

        for (int i = 0; i < list.size(); i++)
        {
            T t = list.get(i);
            infos.add(new Info<>(t, query, keyGetter.apply(t), priority.get(i),
                    simCoef, matchCoef, priorCoef));
        }
        infos.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));

        list.clear();
        for (var i : infos) list.add(i.value);

        return infos;
    }

    /**
     * 对于类的排序
     * @param list 要排序的列表
     * @param priority 每一个元素的优先级，传入null就是优先级一样
     * @param keyGetter 列表元素如何获得排序关键词
     * @param query 要匹配的字符串
     * @param options 三个参数，分别为相似度权重、匹配度权重、优先级权重，加起来不等于0，传入null就是权重相等
     * */
    public static <T> List<Info<T>> sort(
            List<T> list, List<Double> priority, Function<T, String> keyGetter, String query,
            Triple<Double, Double, Double> options
    )
    {
        if (options == null) options = Triple.of(1.0, 1.0, 1.0);
        if (priority == null) priority = Collections.nCopies(list.size(), 1.0);

        return sort(list, priority, keyGetter, query, options.getLeft(), options.getMiddle(), options.getRight());
    }

    /**
     * 对于字符串列表的排序
     * @param list 要排序的字符串列表
     * @param priority 每一个元素的优先级，传入null就是优先级一样
     * @param query 要匹配的字符串
     * @param options 三个参数，分别为相似度权重、匹配度权重、优先级权重，加起来不等于0，传入null就是权重相等
     * */
    public static List<Info<String>> sort(
            List<String> list, List<Double> priority, String query,
            Triple<Double, Double, Double> options
    )
    {
        if (options == null) options = Triple.of(1.0, 1.0, 1.0);
        if (priority == null) priority = Collections.nCopies(list.size(), 1.0);

        return sort(list, priority, s -> s, query, options);
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

            for (var i : WeightSort.sort(set, null, query, null))
            {
                System.out.println(i);
            }
        }
    }
}
