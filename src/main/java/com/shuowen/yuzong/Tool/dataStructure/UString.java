package com.shuowen.yuzong.Tool.dataStructure;

import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.dataStructure.error.IllegalStringException;

import java.util.*;

/**
 * 增强版基于代理对的字符串类，旨在封装掉所有和代码点有关的内容，专为字典应用设计，
 * 提供完整的Unicode支持和高性能操作
 *
 * @apiNote 在这里一个字符用String装，一个字符串用UString装
 */
public class UString implements Iterable<String>, Comparable<UString>
{
    private final StringBuilder str;
    private int size;

    /**
     * 无参数构造方法
     */
    public UString()
    {
        str = new StringBuilder();
        size = 0;
    }

    /**
     * 字符串构造方法
     */
    public UString(String s)
    {
        str = new StringBuilder(s);
        size = computeSize(s);
    }

    public static UString of()
    {
        return new UString();
    }

    public static UString of(String s)
    {
        return new UString(s);
    }

    public int length()
    {
        return size;
    }

    /**
     * 计算外来字符串的代码点长度是多少
     */
    private int computeSize(String s)
    {
        return s.codePointCount(0, s.length());
    }

    /**
     * 是否为空
     */
    public boolean isEmpty()
    {
        return str.isEmpty();
    }

    public void clear()
    {
        str.setLength(0);
        size = 0;
    }

    @JsonValue
    @Override
    public String toString()
    {
        return str.toString();
    }

    /**
     * 代码点索引换成字符索引
     */
    private int mapIndex(int idx)
    {
        if (size == idx) return str.length();
        if (idx < 0 || idx > size) throw new IndexOutOfBoundsException("超范围：" + idx);

        //快指针是字符索引，慢指针是代码点索引
        int fast = 0, slow = 0;

        // 当代码点索引到达需求的时候退出，因为找到了。
        // 当代码字符索引到底的时候退出，不然会报错
        while (slow < idx && fast < str.length())
        {
            int point = str.codePointAt(fast);
            fast += Character.charCount(point);
            slow++;
        }
        return fast;
    }

    /**
     * 在idx号码索引之前插入数据
     */
    public void insert(int idx, String s)
    {
        str.insert(mapIndex(idx), s);
        size += computeSize(s);
    }

    /**
     * 在idx号码索引之前插入数据
     */
    public void insert(int idx, UString s)
    {
        str.insert(mapIndex(idx), s.toString());
        size += s.length();
    }

    public void append(String s)
    {
        str.append(s);
        size += computeSize(s);
    }

    public void append(UString s)
    {
        str.append(s.toString());
        size += s.length();
    }

    public void delete(int start, int end)
    {
        str.delete(mapIndex(start), mapIndex(end));
        size -= (end - start);
    }

    public UString substring(int start, int end)
    {
        return new UString(str.substring(mapIndex(start), mapIndex(end)));
    }

    public String at(int idx)
    {
        return new String(Character.toChars(str.codePointAt(mapIndex(idx))));
    }

    @Override
    public int compareTo(UString o)
    {
        int l1 = size, l2 = o.size;
        int minl = Math.min(l1, l2);

        for (int i = 0; i < minl; i++)
        {
            if (!at(i).equals(o.at(i))) return at(i).compareTo(o.at(i));
        }
        return l1 - l2;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UString that = (UString) o;
        if (size != that.size) return false;

        return str.toString().contentEquals(that.str);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(str, size);
    }


    @Override
    public Iterator<String> iterator()
    {
        return new Iterator<>()
        {
            private int index = 0;

            @Override
            public boolean hasNext()
            {
                return index < size;
            }

            @Override
            public String next()
            {
                if (!hasNext()) throw new NoSuchElementException();
                String ch = at(index);
                index++;
                return ch;
            }
        };
    }

    public static List<String> toUCharList(String str)
    {
        List<String> ans = new ArrayList<>();
        for (var i : UString.of(str)) ans.add(i);
        return ans;
    }

    public static int getULen(String str)
    {
        return str.codePointCount(0, str.length());
    }

    public static boolean isUChar(String str)
    {
        return getULen(str) == 1;
    }

    public static void checkUChar(ErrorInfo note, String... str)
    {
        List<String> log = new ArrayList<>();
        for (String s : str) if (!isUChar(s)) log.add(s);

        if (!log.isEmpty())
            throw new IllegalStringException(String.format("""
                    异常：字符串不是单个UTF-16字符。not a character.
                    内容：%s
                    上下文：%s
                    """, log, note.info())
            );
    }

    public static boolean isLenEqual(String str1, String str2)
    {
        return getULen(str1) == getULen(str2);
    }

    public static void checkLenEqual(ErrorInfo note, String str1, String str2)
    {
        if (!isLenEqual(str1, str2))
            throw new IllegalStringException(String.format("""
                    异常：两段文本UTF-16字符长度不相等。length not equal.
                    （长度%s）:%s
                    （长度%s）:%s
                    上下文：%s
                    """, str1.length(), str1, str2.length(), str2, note.info())
            );
    }

}
