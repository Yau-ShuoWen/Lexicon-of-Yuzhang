package com.shuowen.yuzong.Tool.dataStructure;

import java.util.*;

/**
 * 增强版基于代理对的字符串类，专为字典应用设计
 * 提供完整的Unicode支持和高性能操作
 */
public class UnicodeString implements Iterable<String>, Comparable<UnicodeString>
{
    private StringBuilder content;
    private int codePointCount = -1; // 缓存代码点数量，-1表示需要重新计算

    // 构造方法
    public UnicodeString()
    {
        content = new StringBuilder();
        codePointCount = 0;
    }

    public UnicodeString(String str)
    {
        content = new StringBuilder(str);
        codePointCount = -1; // 需要重新计算
    }

    public UnicodeString(CharSequence charSequence)
    {
        content = new StringBuilder(charSequence);
        codePointCount = -1; // 需要重新计算
    }

    public UnicodeString(int[] codePoints)
    {
        content = new StringBuilder();
        for (int codePoint : codePoints)
        {
            content.append(Character.toChars(codePoint));
        }
        codePointCount = codePoints.length;
    }

    // 获取代码点数量（字符数量，代理对算作一个字符）
    public int codePointCount()
    {
        if (codePointCount < 0)
        {
            codePointCount = content.codePointCount(0, content.length());
        }
        return codePointCount;
    }

    // 获取代码单元数量（char数量）
    public int length()
    {
        return content.length();
    }

    // 检查是否为空
    public boolean isEmpty()
    {
        return content.isEmpty();
    }

    // 清空字符串
    public void clear()
    {
        content.setLength(0);
        codePointCount = 0;
    }

    // 在指定代码点位置插入一个代码点
    public void insert(int index, int codePoint)
    {
        int charIndex = codePointIndexToCharIndex(index);
        content.insert(charIndex, Character.toChars(codePoint));
        if (codePointCount >= 0) codePointCount++;
    }

    // 在指定位置插入字符串
    public void insert(int index, UnicodeString str)
    {
        int charIndex = codePointIndexToCharIndex(index);
        content.insert(charIndex, str.content);
        if (codePointCount >= 0) codePointCount += str.codePointCount();
    }

    // 在指定位置插入普通字符串
    public void insert(int index, String str)
    {
        int charIndex = codePointIndexToCharIndex(index);
        content.insert(charIndex, str);
        codePointCount = -1; // 需要重新计算
    }

    // 在末尾添加一个代码点
    public void append(int codePoint)
    {
        content.append(Character.toChars(codePoint));
        if (codePointCount >= 0) codePointCount++;
    }

    // 在末尾添加字符串
    public void append(UnicodeString str)
    {
        content.append(str.content);
        if (codePointCount >= 0) codePointCount += str.codePointCount();
    }

    // 在末尾添加普通字符串
    public void append(String str)
    {
        content.append(str);
        codePointCount = -1; // 需要重新计算
    }

    // 删除指定代码点位置的字符
    public void delete(int index)
    {
        int charIndex = codePointIndexToCharIndex(index);
        int codePoint = content.codePointAt(charIndex);
        int charCount = Character.charCount(codePoint);
        content.delete(charIndex, charIndex + charCount);
        if (codePointCount >= 0) codePointCount--;
    }

    // 删除指定范围的代码点
    public void delete(int start, int end)
    {
        int startCharIndex = codePointIndexToCharIndex(start);
        int endCharIndex = codePointIndexToCharIndex(end);
        content.delete(startCharIndex, endCharIndex);
        if (codePointCount >= 0) codePointCount -= (end - start);
    }

    // 获取指定代码点位置的字符
    public int codePointAt(int index)
    {
        int charIndex = codePointIndexToCharIndex(index);
        return content.codePointAt(charIndex);
    }

    // 获取子字符串
    public UnicodeString substring(int start)
    {
        return substring(start, codePointCount());
    }

    // 获取子字符串
    public UnicodeString substring(int start, int end)
    {
        int startCharIndex = codePointIndexToCharIndex(start);
        int endCharIndex = codePointIndexToCharIndex(end);
        return new UnicodeString(content.substring(startCharIndex, endCharIndex));
    }

    // 检查是否以指定字符串开头
    public boolean startsWith(UnicodeString prefix)
    {
        if (prefix.codePointCount() > codePointCount()) return false;

        for (int i = 0; i < prefix.codePointCount(); i++)
        {
            if (codePointAt(i) != prefix.codePointAt(i))
            {
                return false;
            }
        }
        return true;
    }

    // 检查是否以指定字符串结尾
    public boolean endsWith(UnicodeString suffix)
    {
        if (suffix.codePointCount() > codePointCount()) return false;

        int offset = codePointCount() - suffix.codePointCount();
        for (int i = 0; i < suffix.codePointCount(); i++)
        {
            if (codePointAt(offset + i) != suffix.codePointAt(i))
            {
                return false;
            }
        }
        return true;
    }

    // 查找代码点第一次出现的位置
    public int indexOf(int codePoint)
    {
        return indexOf(codePoint, 0);
    }

    // 从指定位置开始查找代码点
    public int indexOf(int codePoint, int fromIndex)
    {
        if (fromIndex < 0) fromIndex = 0;

        for (int i = fromIndex; i < codePointCount(); i++)
        {
            if (codePointAt(i) == codePoint)
            {
                return i;
            }
        }
        return -1;
    }

    // 查找子字符串第一次出现的位置
    public int indexOf(UnicodeString str)
    {
        return indexOf(str, 0);
    }

    // 从指定位置开始查找子字符串
    public int indexOf(UnicodeString str, int fromIndex)
    {
        if (str.isEmpty()) return fromIndex;
        if (fromIndex < 0) fromIndex = 0;

        int maxIndex = codePointCount() - str.codePointCount();
        for (int i = fromIndex; i <= maxIndex; i++)
        {
            boolean match = true;
            for (int j = 0; j < str.codePointCount(); j++)
            {
                if (codePointAt(i + j) != str.codePointAt(j))
                {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }
        return -1;
    }

    // 转换为代码点数组
    public int[] toCodePointArray()
    {
        int[] codePoints = new int[codePointCount()];
        int charIndex = 0;
        for (int i = 0; i < codePoints.length; i++)
        {
            int codePoint = content.codePointAt(charIndex);
            codePoints[i] = codePoint;
            charIndex += Character.charCount(codePoint);
        }
        return codePoints;
    }

    // 转换为普通字符串
    @Override
    public String toString()
    {
        return content.toString();
    }

    // 比较方法
    @Override
    public int compareTo(UnicodeString other)
    {
        int len1 = codePointCount();
        int len2 = other.codePointCount();
        int minLen = Math.min(len1, len2);

        for (int i = 0; i < minLen; i++)
        {
            int cp1 = codePointAt(i);
            int cp2 = other.codePointAt(i);
            if (cp1 != cp2)
            {
                return cp1 - cp2;
            }
        }

        return len1 - len2;
    }

    // 相等性比较
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UnicodeString other = (UnicodeString) obj;
        if (codePointCount() != other.codePointCount()) return false;

        for (int i = 0; i < codePointCount(); i++)
        {
            if (codePointAt(i) != other.codePointAt(i))
            {
                return false;
            }
        }
        return true;
    }

    // 哈希码计算
    @Override
    public int hashCode()
    {
        int hash = 0;
        for (int i = 0; i < Math.min(codePointCount(), 16); i++)
        {
            hash = 31 * hash + codePointAt(i);
        }
        return hash;
    }

    // 迭代器实现 - 现在返回字符串而不是整数
    @Override
    public Iterator<String> iterator()
    {
        return new CodePointStringIterator();
    }

    // 代码点值迭代器（返回整数代码点）
    public Iterator<Integer> codePointIterator()
    {
        return new CodePointValueIterator();
    }

    // 反向字符串迭代器
    public Iterator<String> reverseIterator()
    {
        return new ReverseCodePointStringIterator();
    }

    // 反向代码点值迭代器
    public Iterator<Integer> reverseCodePointValueIterator()
    {
        return new ReverseCodePointValueIterator();
    }

    // 将代码点索引转换为字符索引
    private int codePointIndexToCharIndex(int codePointIndex)
    {
        if (codePointIndex < 0 || codePointIndex > codePointCount())
        {
            throw new IndexOutOfBoundsException("Code point index out of bounds: " + codePointIndex);
        }

        // 如果代码点计数已缓存，可以优化计算
        if (codePointCount >= 0 && codePointIndex == codePointCount)
        {
            return content.length();
        }

        int charIndex = 0;
        int currentCodePointIndex = 0;

        while (currentCodePointIndex < codePointIndex && charIndex < content.length())
        {
            int codePoint = content.codePointAt(charIndex);
            charIndex += Character.charCount(codePoint);
            currentCodePointIndex++;
        }

        return charIndex;
    }

    // 代码点字符串迭代器（返回字符串）
    private class CodePointStringIterator implements Iterator<String>
    {
        private int currentCharIndex = 0;

        @Override
        public boolean hasNext()
        {
            return currentCharIndex < content.length();
        }

        @Override
        public String next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }

            int codePoint = content.codePointAt(currentCharIndex);
            currentCharIndex += Character.charCount(codePoint);
            return new String(Character.toChars(codePoint));
        }
    }

    // 代码点值迭代器（返回整数）
    private class CodePointValueIterator implements Iterator<Integer>
    {
        private int currentCharIndex = 0;

        @Override
        public boolean hasNext()
        {
            return currentCharIndex < content.length();
        }

        @Override
        public Integer next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }

            int codePoint = content.codePointAt(currentCharIndex);
            currentCharIndex += Character.charCount(codePoint);
            return codePoint;
        }
    }

    // 反向代码点字符串迭代器
    private class ReverseCodePointStringIterator implements Iterator<String>
    {
        private int currentCharIndex;

        public ReverseCodePointStringIterator()
        {
            currentCharIndex = content.length();
        }

        @Override
        public boolean hasNext()
        {
            return currentCharIndex > 0;
        }

        @Override
        public String next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }

            // 找到前一个代码点的起始位置
            int codePoint = content.codePointBefore(currentCharIndex);
            currentCharIndex -= Character.charCount(codePoint);
            return new String(Character.toChars(codePoint));
        }
    }

    // 反向代码点值迭代器
    private class ReverseCodePointValueIterator implements Iterator<Integer>
    {
        private int currentCharIndex;

        public ReverseCodePointValueIterator()
        {
            currentCharIndex = content.length();
        }

        @Override
        public boolean hasNext()
        {
            return currentCharIndex > 0;
        }

        @Override
        public Integer next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }

            // 找到前一个代码点的起始位置
            int codePoint = content.codePointBefore(currentCharIndex);
            currentCharIndex -= Character.charCount(codePoint);
            return codePoint;
        }
    }

    // 构建器模式，便于链式调用
    public static class Builder
    {
        private UnicodeString string;

        public Builder()
        {
            string = new UnicodeString();
        }

        public Builder append(int codePoint)
        {
            string.append(codePoint);
            return this;
        }

        public Builder append(UnicodeString str)
        {
            string.append(str);
            return this;
        }

        public Builder append(String str)
        {
            string.append(str);
            return this;
        }

        public Builder insert(int index, int codePoint)
        {
            string.insert(index, codePoint);
            return this;
        }

        public UnicodeString build()
        {
            return string;
        }
    }

    // 示例用法和测试
    public static void main(String[] args)
    {
        // 创建字符串
        UnicodeString str = new UnicodeString("Hello");
        System.out.println("初始字符串: " + str);
        System.out.println("代码点数量: " + str.codePointCount());

        // 添加表情符号（代理对）
        str.append(0x1F600); // 笑脸
        str.append(0x1F601); // 笑脸眼睛
        System.out.println("添加表情后: " + str);
        System.out.println("代码点数量: " + str.codePointCount());

        // 使用构建器
        UnicodeString builtStr = new UnicodeString.Builder()
                .append("前缀")
                .append(0x1F609) // 眨眼表情
                .append("后缀")
                .build();
        System.out.println("构建的字符串: " + builtStr);

        // 迭代代码点字符串 - 这是修改后的主要功能
        System.out.println("迭代代码点字符串:");
        for (String s : str)
        {
            System.out.println(s);
        }

        // 迭代代码点值（如果需要的话）
        System.out.println("迭代代码点值:");
        Iterator<Integer> codePointIt = str.codePointIterator();
        while (codePointIt.hasNext())
        {
            int codePoint = codePointIt.next();
            System.out.printf("U+%04X %s%n", codePoint, new String(Character.toChars(codePoint)));
        }

        // 反向迭代字符串
        System.out.println("反向迭代字符串:");
        Iterator<String> reverseIt = str.reverseIterator();
        while (reverseIt.hasNext())
        {
            String s = reverseIt.next();
            System.out.println(s);
        }

        // 子字符串操作
        UnicodeString subStr = str.substring(5, 7);
        System.out.println("子字符串(5-7): " + subStr);

        // 查找操作
        int index = str.indexOf(0x1F600);
        System.out.println("笑脸表情位置: " + index);

        // 比较操作
        UnicodeString str2 = new UnicodeString("Hello😀");
        System.out.println("比较结果: " + str.compareTo(str2));
        System.out.println("相等性: " + str.equals(str2));
    }
}