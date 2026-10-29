package com.shuowen.yuzong.util.text;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Getter
public class ScTcRegex
{
    /** Java 包含匹配正则。 */
    private final String regex;
    /** Java 完整匹配正则。 */
    private final String fullRegex;
    /** MySQL 包含匹配正则。 */
    private final String sqlRegex;
    /** MySQL 完整匹配正则。 */
    private final String fullSqlRegex;

    @Getter(AccessLevel.NONE)
    private final Pattern pattern;
    @Getter(AccessLevel.NONE)
    private final Pattern fullPattern;

    private static final Map<UChar, List<String>> VARIANT_CACHE = new ConcurrentHashMap<>();

    /** 传入待搜索的文本，按 Unicode 代码点逐字展开。 */
    public ScTcRegex(UString str)
    {
        Objects.requireNonNull(str, "str");
        List<List<String>> segments = new ArrayList<>(str.length());
        for (UChar ch : str)
        {
            segments.add(expand(ch));
        }

        regex = toJavaRegex(segments);
        fullRegex = "^" + regex + "$";
        sqlRegex = toMySqlRegex(segments);
        fullSqlRegex = "^" + sqlRegex + "$";
        pattern = Pattern.compile(regex);
        fullPattern = Pattern.compile(fullRegex);
    }

    /** 整个字符串是否完整满足简繁序列。 */
    public boolean matches(String text)
    {
        Objects.requireNonNull(text, "text");
        return fullPattern.matcher(text).matches();
    }

    /** 字符串中是否存在满足简繁序列的部分。 */
    public boolean contains(String text)
    {
        Objects.requireNonNull(text, "text");
        return pattern.matcher(text).find();
    }

    /** 生成不带首尾限制的 Java 正则主体。 */
    private static String toJavaRegex(List<List<String>> segments)
    {
        StringBuilder sb = new StringBuilder();
        for (List<String> seg : segments)
        {
            if (seg.size() == 1)
            {
                sb.append(Pattern.quote(seg.get(0)));
                continue;
            }
            sb.append('(');
            for (int i = 0; i < seg.size(); i++)
            {
                if (i > 0) sb.append("|");
                sb.append(Pattern.quote(seg.get(i))); // Java 安全转义
            }
            sb.append(')');
        }
        return sb.toString();
    }

    /** 生成通过 JDBC/MyBatis 参数传入的 MySQL 8 ICU 正则主体。 */
    private static String toMySqlRegex(List<List<String>> segments)
    {
        StringBuilder sb = new StringBuilder();
        for (List<String> seg : segments)
        {
            if (seg.size() == 1)
            {
                sb.append(escapeForMySqlRegex(seg.get(0)));
                continue;
            }
            sb.append('(');
            for (int i = 0; i < seg.size(); i++)
            {
                if (i > 0) sb.append('|');
                sb.append(escapeForMySqlRegex(seg.get(i)));
            }
            sb.append(')');
        }
        return sb.toString();
    }

    /**
     * 转义单个候选词：
     * 1. 先转义正则元字符（加反斜杠）
     * 2. 再把反斜杠双写，供 MySQL 字符串字面量使用
     * 注意：如果通过 JDBC PreparedStatement 传参，第2步不需要做！
     */
    private static String escapeForMySqlRegex(String word)
    {
        // 正则元字符
        String meta = "\\^$.|?*+()[]{}";
        StringBuilder sb = new StringBuilder();
        for (UChar ch : UString.of(word))
        {
            if (ch.codePoint() <= Character.MAX_VALUE && meta.indexOf(ch.codePoint()) >= 0)
            {
                sb.append('\\'); // 加正则转义
            }
            sb.append(ch);
        }
        // 如果直接拼进 SQL 字符串，需要把 \ 变成 \\（JDBC 参数则不需要）
        return sb.toString();
    }

    private static List<String> expand(UChar source)
    {
        return VARIANT_CACHE.computeIfAbsent(source, ScTcRegex::computeVariants);
    }

    private static List<String> computeVariants(UChar source)
    {
        ScTcCharacterConverter.Forms forms = ScTcCharacterConverter.lookup(source);
        LinkedHashSet<String> values = new LinkedHashSet<>();
        forms.simplified().forEach(ch -> values.add(ch.toString()));
        forms.traditional().forEach(ch -> values.add(ch.toString()));
        forms.variants().forEach(ch -> values.add(ch.toString()));
        return List.copyOf(values);
    }

    /** 保留旧入口；原始方向数据由独立的单字字表管理。 */
    public static Map<String, Map<UChar, List<UChar>>> sourceMappings()
    {
        return ScTcCharacterData.sourceMappings();
    }

    public static Map<UChar, List<UChar>> contextRelatedMappings()
    {
        return ScTcCharacterData.contextRelatedMappings();
    }
}
