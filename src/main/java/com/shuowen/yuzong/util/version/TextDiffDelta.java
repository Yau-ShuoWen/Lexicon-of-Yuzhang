package com.shuowen.yuzong.util.version;

/**
 * 一段文本差异，位置使用 Unicode 代码点索引，而不是 Java char 索引。
 */
public record TextDiffDelta(
        ChangeType type,
        int sourceStart,
        int sourceEnd,
        int targetStart,
        int targetEnd,
        String sourceText,
        String targetText
)
{
}
