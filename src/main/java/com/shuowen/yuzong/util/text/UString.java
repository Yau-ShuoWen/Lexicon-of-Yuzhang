package com.shuowen.yuzong.util.text;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.shuowen.yuzong.util.ext.other.NullTool.checkNotNull;

/**
 * 增强版基于代理对的字符串类，封装掉所有和代码点有关的内容，提供完整的Unicode支持
 * 底层使用int数组存储Unicode代码点
 *
 * @apiNote 在这里一个字符用String装，一个字符串用UString装
 */
public class UString implements Iterable<UChar>, UElement<UString>
{
    private int[] codePoints;
    private int size;

    /**
     * 无参数构造方法
     */
    public UString()
    {
        codePoints = new int[0];
        size = 0;
    }

    /**
     * 字符串构造方法
     */
    public UString(String s)
    {
        checkNotNull(s);
        this.codePoints = s.codePoints().toArray();
        this.size = codePoints.length;
    }

    /**
     * 从代码点数组构造
     */
    private UString(int[] codePoints)
    {
        this.codePoints = Arrays.copyOf(codePoints, codePoints.length);
        this.size = codePoints.length;
    }

    public static UString of()
    {
        return new UString();
    }

    @JsonCreator
    public static UString of(String s)
    {
        return new UString(s);
    }

    public static UString valueOf(String s)
    {
        return new UString(s);
    }

    public int length()
    {
        return size;
    }

    /**
     * 是否为空
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    public void clear()
    {
        codePoints = new int[0];
        size = 0;
    }

    @JsonValue
    @Override
    public String toString()
    {
        return new String(codePoints, 0, size);
    }

    @Override
    public UString toUString()
    {
        return this;
    }

    /**
     * 检查索引是否有效
     */
    private void checkIndex(int idx)
    {
        if (idx < 0 || idx > size)
        {
            throw new IndexOutOfBoundsException("超范围：" + idx);
        }
    }

    /**
     * 在idx索引之前插入数据
     */
    public void insert(int idx, String s)
    {
        checkNotNull(s);
        checkIndex(idx);
        int[] insertPoints = s.codePoints().toArray();
        insertCodePoints(idx, insertPoints);
    }

    /**
     * 在idx索引之前插入数据
     */
    public void insert(int idx, UString s)
    {
        checkNotNull(s);
        checkIndex(idx);
        insertCodePoints(idx, s.codePoints);
    }

    public void insert(int idx, UChar c)
    {
        checkNotNull(c);
        checkIndex(idx);
        insertCodePoints(idx, new int[]{c.codePoint()});
    }

    /**
     * 在指定位置插入代码点数组
     */
    private void insertCodePoints(int idx, int[] insertPoints)
    {
        int insertLen = insertPoints.length;
        if (insertLen == 0) return;

        int[] newArray = new int[size + insertLen];
        System.arraycopy(codePoints, 0, newArray, 0, idx);
        System.arraycopy(insertPoints, 0, newArray, idx, insertLen);
        System.arraycopy(codePoints, idx, newArray, idx + insertLen, size - idx);
        codePoints = newArray;
        size += insertLen;
    }

    public void append(String s)
    {
        checkNotNull(s);
        int[] appendPoints = s.codePoints().toArray();
        appendCodePoints(appendPoints);
    }

    public void append(UString s)
    {
        checkNotNull(s);
        appendCodePoints(s.codePoints);
    }

    public void append(UChar c)
    {
        checkNotNull(c);
        appendCodePoints(new int[]{c.codePoint()});
    }

    /**
     * 追加代码点数组
     */
    private void appendCodePoints(int[] appendPoints)
    {
        int appendLen = appendPoints.length;
        if (appendLen == 0) return;

        int[] newArray = Arrays.copyOf(codePoints, size + appendLen);
        System.arraycopy(appendPoints, 0, newArray, size, appendLen);
        codePoints = newArray;
        size += appendLen;
    }

    public void delete(int start, int end)
    {
        checkIndex(start);
        checkIndex(end);
        if (start >= end) return;

        int deleteLen = end - start;
        int[] newArray = new int[size - deleteLen];
        System.arraycopy(codePoints, 0, newArray, 0, start);
        System.arraycopy(codePoints, end, newArray, start, size - end);
        codePoints = newArray;
        size -= deleteLen;
    }

    public UString substring(int start, int end)
    {
        checkIndex(start);
        checkIndex(end);
        if (start > end)
        {
            throw new IndexOutOfBoundsException("start > end");
        }
        return new UString(Arrays.copyOfRange(codePoints, start, end));
    }

    public String at(int idx)
    {
        checkIndex(idx);
        if (idx == size)
        {
            throw new IndexOutOfBoundsException("索引越界：" + idx);
        }
        return new String(new int[]{codePoints[idx]}, 0, 1);
    }

    public UChar uCharAt(int idx)
    {
        checkIndex(idx);
        if (idx == size)
        {
            throw new IndexOutOfBoundsException("索引越界：" + idx);
        }
        return UChar.of(codePoints[idx]);
    }

    @Override
    public int compareTo(UString o)
    {
        int l1 = size, l2 = o.size;
        int minl = Math.min(l1, l2);

        for (int i = 0; i < minl; i++)
        {
            if (codePoints[i] != o.codePoints[i])
            {
                return Integer.compare(codePoints[i], o.codePoints[i]);
            }
        }
        return l1 - l2;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UString that = (UString) o;
        return size == that.size && Arrays.equals(codePoints, that.codePoints);
    }

    @Override
    public boolean contentEquals(char c)
    {
        return size == 1 && codePoints[0] == c;
    }

    @Override
    public boolean contentEquals(CharSequence s)
    {
        if (s == null) return false;

        int otherLen = Character.codePointCount(s, 0, s.length());
        if (size != otherLen) return false;

        int idx = 0;
        for (int i = 0; i < s.length(); )
        {
            int cp = Character.codePointAt(s, i);
            if (cp != codePoints[idx]) return false;
            idx++;
            i += Character.charCount(cp);
        }
        return true;
    }

    @Override
    public boolean contentEquals(UElement<?> other)
    {
        if (other == null) return false;
        if (other instanceof UString us)
        {
            return Arrays.equals(codePoints, us.codePoints);
        }
        else if (other instanceof UChar uc)
        {
            return size == 1 && codePoints[0] == uc.codePoint();
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(codePoints);
    }

    @Override
    public Iterator<UChar> iterator()
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
            public UChar next()
            {
                if (!hasNext()) throw new NoSuchElementException();
                UChar ch = uCharAt(index);
                index++;
                return ch;
            }
        };
    }

    public Iterable<String> chars()
    {
        return () -> new Iterator<>()
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

    public java.util.List<String> toCharsList()
    {
        java.util.List<String> ans = new java.util.ArrayList<>();
        for (var i : chars()) ans.add(i);
        return ans;
    }

    public UString handle(Function<String, String> fun)
    {
        checkNotNull(fun);
        String result = fun.apply(toString());
        checkNotNull(result);

        this.codePoints = result.codePoints().toArray();
        this.size = this.codePoints.length;
        return this;
    }
}