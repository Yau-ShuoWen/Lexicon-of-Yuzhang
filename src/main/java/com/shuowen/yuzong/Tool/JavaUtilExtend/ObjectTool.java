package com.shuowen.yuzong.Tool.JavaUtilExtend;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class ObjectTool
{
    /**
     * 这句话为了对于那些偶尔测试一次的测试文件，打包的时候跳过测试，只能在测试文件里使用
     * <blockquote><pre>
     * if (ObjectTool.unchecked(true)) return;
     * </pre></blockquote>
     * 设计出来的目的就是：<p>
     * - 如果写成 {@code return;}，编译器直接报错<br>
     * - 如果写成 {@code if(true) return;}，编译器报出强警告<br>
     * - 这里特意设计了一个不容易推断的内容，避免直接警告
     */
    public static boolean unchecked(boolean b)
    {
        System.out.println(b ? "未打开测试（打包过程中请忽略）" : "打开测试");
        int a = 0;
        if (b) a++;
        return a == 1;
    }

    /**
     * 使用空格输出多个内容
     */
    public static void print(Object... obj)
    {
        for (Object o : obj) System.out.print(o + " ");
        System.out.println();
    }

    /**
     * 使用换行输出多个内容
     */
    public static void println(Object... obj)
    {
        for (Object o : obj) System.out.println(o);
        System.out.println();
    }

    @SafeVarargs
    public static <T> boolean allEqual(T... values)
    {
        if (values == null || values.length <= 1) return true;
        for (T value : values) if (!Objects.equals(values[0], value)) return false;
        return true;
    }

    @SafeVarargs
    public static <T> boolean existEqual(T pattern, T... values)
    {
        if (values == null || values.length < 1) return false;
        for (T value : values) if (Objects.equals(pattern, value)) return true;
        return false;
    }

    public static <T> boolean allEqual(Collection<T> values)
    {
        if (values == null || values.size() <= 1) return true;

        Iterator<T> it = values.iterator();
        T first = it.next();
        while (it.hasNext()) if (!Objects.equals(first, it.next())) return false;
        return true;
    }

    public static <T> boolean existEqual(T pattern, Collection<T> values)
    {
        if (values == null || values.isEmpty()) return false;
        Iterator<T> it = values.iterator();
        T first = it.next();
        while (it.hasNext()) if (Objects.equals(pattern, first)) return true;
        return false;
    }
}
