package com.shuowen.yuzong.util.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/** 独立于 Spring 和数据库的单字简繁、异体字表。 */
public final class ScTcCharacterData
{
    private static final Map<String, Map<UChar, List<UChar>>> SOURCES = loadSources();
    private static final Map<UChar, List<UChar>> CONTEXT_RELATED =
            readUnihan("/text/sctc/unihan-specializedsemanticvariant.txt", "kSpecializedSemanticVariant");
    private static final Map<UChar, List<UChar>> COMPONENTS = buildComponents();

    private ScTcCharacterData() {}

    /** 原始方向及来源分别保留；此处没有做并查集合并。 */
    public static Map<String, Map<UChar, List<UChar>>> sourceMappings()
    {
        return SOURCES;
    }

    /** 仅在特定语境下相关的字形关系，不用于无条件模糊匹配。 */
    public static Map<UChar, List<UChar>> contextRelatedMappings()
    {
        return CONTEXT_RELATED;
    }

    /** 搜索用无向连通分量，未知字只与自身对应。 */
    public static List<UChar> equivalents(UChar ch)
    {
        return COMPONENTS.getOrDefault(ch, List.of(ch));
    }

    private static Map<String, Map<UChar, List<UChar>>> loadSources()
    {
        Map<String, Map<UChar, List<UChar>>> sources = new LinkedHashMap<>();
        sources.put("opencc-st", readOpenCc("/text/sctc/opencc-st-characters.txt"));
        sources.put("opencc-ts", readOpenCc("/text/sctc/opencc-ts-characters.txt"));
        sources.put("opencc-tw-variants", readOpenCc("/text/sctc/opencc-tw-variants.txt"));
        sources.put("opencc-hk-variants", readOpenCc("/text/sctc/opencc-hk-variants.txt"));
        sources.put("unihan-simplified", readUnihan("/text/sctc/unihan-simplifiedvariant.txt", "kSimplifiedVariant"));
        sources.put("unihan-traditional", readUnihan("/text/sctc/unihan-traditionalvariant.txt", "kTraditionalVariant"));
        sources.put("unihan-z-variant", readUnihan("/text/sctc/unihan-zvariant.txt", "kZVariant"));
        sources.put("unihan-semantic-variant", readUnihan("/text/sctc/unihan-semanticvariant.txt", "kSemanticVariant"));
        sources.put("manual", readManual("/text/sctc/manual.txt"));
        return Collections.unmodifiableMap(sources);
    }

    private static Map<UChar, List<UChar>> buildComponents()
    {
        Map<UChar, LinkedHashSet<UChar>> graph = new LinkedHashMap<>();
        for (Map.Entry<String, Map<UChar, List<UChar>>> dataset : SOURCES.entrySet())
        {
            // 同义异体不一定能逐字替换，保留原始数据但不用于转换或搜索。
            if (dataset.getKey().equals("unihan-semantic-variant")) continue;
            Map<UChar, List<UChar>> source = dataset.getValue();
            for (Map.Entry<UChar, List<UChar>> entry : source.entrySet())
            {
                UChar key = entry.getKey();
                graph.computeIfAbsent(key, ignored -> new LinkedHashSet<>()).addAll(entry.getValue());
                for (UChar value : entry.getValue())
                    graph.computeIfAbsent(value, ignored -> new LinkedHashSet<>()).add(key);
            }
        }

        Map<UChar, List<UChar>> result = new LinkedHashMap<>();
        Set<UChar> visited = new LinkedHashSet<>();
        for (UChar start : graph.keySet())
        {
            if (!visited.add(start)) continue;
            List<UChar> component = new ArrayList<>();
            Queue<UChar> queue = new ArrayDeque<>();
            queue.add(start);
            while (!queue.isEmpty())
            {
                UChar current = queue.remove();
                component.add(current);
                for (UChar neighbour : graph.get(current))
                    if (visited.add(neighbour)) queue.add(neighbour);
            }
            List<UChar> values = List.copyOf(component);
            for (UChar ch : values) result.put(ch, values);
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<UChar, List<UChar>> readOpenCc(String resource)
    {
        Map<UChar, LinkedHashSet<UChar>> data = new LinkedHashMap<>();
        int lineNumber = 0;
        for (String raw : readLines(resource))
        {
            lineNumber++;
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] fields = line.split("\\s+");
            if (fields.length < 2 || UString.of(fields[0]).length() != 1)
                throw new IllegalStateException(resource + ":" + lineNumber + " 字表格式错误");
            UChar key = UChar.of(fields[0]);
            LinkedHashSet<UChar> values = data.computeIfAbsent(key, ignored -> new LinkedHashSet<>());
            for (int i = 1; i < fields.length; i++)
            {
                if (UString.of(fields[i]).length() != 1)
                    throw new IllegalStateException(resource + ":" + lineNumber + " 非单字映射");
                values.add(UChar.of(fields[i]));
            }
        }
        return freeze(data);
    }

    private static Map<UChar, List<UChar>> readUnihan(String resource, String property)
    {
        Map<UChar, LinkedHashSet<UChar>> data = new LinkedHashMap<>();
        int lineNumber = 0;
        for (String raw : readLines(resource))
        {
            lineNumber++;
            if (raw.isBlank() || raw.startsWith("#")) continue;
            String[] fields = raw.split("\\t", -1);
            if (fields.length != 3 || !fields[1].equals(property))
                throw new IllegalStateException(resource + ":" + lineNumber + " Unihan 字表格式错误");
            UChar key = parseCodePoint(fields[0], resource, lineNumber);
            LinkedHashSet<UChar> values = data.computeIfAbsent(key, ignored -> new LinkedHashSet<>());
            for (String value : fields[2].split(" "))
                values.add(parseCodePoint(value.split("<", 2)[0], resource, lineNumber));
        }
        return freeze(data);
    }

    private static UChar parseCodePoint(String value, String resource, int lineNumber)
    {
        try
        {
            if (!value.matches("U\\+[0-9A-F]{4,6}")) throw new NumberFormatException(value);
            return UChar.of(Integer.parseInt(value.substring(2), 16));
        }
        catch (NumberFormatException e)
        {
            throw new IllegalStateException(resource + ":" + lineNumber + " 无效 Unicode 代码点：" + value, e);
        }
    }

    private static Map<UChar, List<UChar>> readManual(String resource)
    {
        Map<UChar, LinkedHashSet<UChar>> data = new LinkedHashMap<>();
        int lineNumber = 0;
        for (String raw : readLines(resource))
        {
            lineNumber++;
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] sides = line.split("<=>", -1);
            if (sides.length != 2 || sides[0].isEmpty() || sides[1].isEmpty())
                throw new IllegalStateException(resource + ":" + lineNumber + " 字表格式错误");
            for (UChar key : UString.of(sides[0]))
            {
                LinkedHashSet<UChar> values = data.computeIfAbsent(key, ignored -> new LinkedHashSet<>());
                for (UChar value : UString.of(sides[1])) values.add(value);
            }
        }
        return freeze(data);
    }

    private static Map<UChar, List<UChar>> freeze(Map<UChar, LinkedHashSet<UChar>> data)
    {
        Map<UChar, List<UChar>> result = new LinkedHashMap<>();
        data.forEach((key, values) -> result.put(key, List.copyOf(values)));
        return Collections.unmodifiableMap(result);
    }

    private static List<String> readLines(String resource)
    {
        try (InputStream stream = ScTcCharacterData.class.getResourceAsStream(resource))
        {
            if (stream == null) throw new IllegalStateException("缺少简繁字表：" + resource);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
            {
                return reader.lines().toList();
            }
        }
        catch (IOException e)
        {
            throw new UncheckedIOException("读取简繁字表失败：" + resource, e);
        }
    }
}
