package com.shuowen.yuzong.Tool.dataStructure;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

import static com.shuowen.yuzong.Tool.JavaUtilExtend.NullTool.checkNotNull;

/**
 * 单个Unicode字符（code point）
 */
public class UChar implements Comparable<UChar>
{
    private final int codePoint;

    private UChar(int codePoint)
    {
        this.codePoint = codePoint;
    }

    /**
     * 从String创建
     */
    public static UChar of(String s)
    {
        checkNotNull(s);
        if (s.codePointCount(0, s.length()) != 1)
            throw new IllegalArgumentException("字符数量不是一个：" + s);

        return new UChar(s.codePointAt(0));
    }

    /**
     * 从codePoint创建
     */
    public static UChar of(int codePoint)
    {
        if (!Character.isValidCodePoint(codePoint))
            throw new IllegalArgumentException("代码点不是正确字符" + codePoint);

        return new UChar(codePoint);
    }

    public int codePoint()
    {
        return codePoint;
    }

    @JsonValue
    @Override
    public String toString()
    {
        return new String(Character.toChars(codePoint));
    }

    @Override
    public int compareTo(UChar o)
    {
        return Integer.compare(codePoint, o.codePoint);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof UChar uChar)) return false;
        return codePoint == uChar.codePoint;
    }

    public boolean contentEquals(UChar c)
    {
        return c != null && codePoint == c.codePoint;
    }

    public boolean contentEquals(char c)
    {
        return codePoint == c;
    }

    public boolean contentEquals(CharSequence s)
    {
        if (s == null) return false;
        if (Character.codePointCount(s, 0, s.length()) != 1)
            return false;

        return codePoint == Character.codePointAt(s, 0);
    }

    public boolean contentEquals(UString s)
    {
        return s != null && s.length() == 1 && codePoint == s.uCharAt(0).codePoint();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(codePoint);
    }
}