package com.shuowen.yuzong.migration;

import com.shuowen.yuzong.linguistics.pinyinv2.lac.LACPinyin;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 检查 GonnyuGeneralIME 南昌话字表中的拼音能否被豫章词解析。
 *
 * <p>本工具只读源 TSV，不修改或清洗任何数据。等确认替换原则后，再在解析前加入显式转换。</p>
 */
class GonnyuLacPinyinAuditTest
{
    private static final Path DEFAULT_SOURCE = Path.of(
            "..", "..", "GonnyuGeneralIME", "resources", "regions", "lancong",
            "dictionaries", "chars.tsv"
    );

    @Test
    void reportUnparseablePinyin() throws IOException
    {
        Path source = sourcePath();
        Map<FailureKey, Failure> failures = new LinkedHashMap<>();
        int dataRowCount = 0;
        int successCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8))
        {
            String headerLine = reader.readLine();
            if (headerLine == null) throw new IllegalStateException("TSV 文件为空：" + source);

            String[] headers = splitTsv(removeBom(headerLine));
            int wordColumn = findColumn(headers, "本词");
            int pinyinColumn = findColumn(headers, "方言拼音");

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                dataRowCount++;
                String[] columns = splitTsv(line);
                String word = column(columns, wordColumn);
                String originalPinyin = column(columns, pinyinColumn).trim();
                String pinyin = normalizeGonnyuPinyin(originalPinyin);

                if (canParse(pinyin))
                {
                    successCount++;
                    continue;
                }

                failures.computeIfAbsent(new FailureKey(originalPinyin, pinyin), ignored -> new Failure())
                        .add(lineNumber, word);
            }
        }

        int failedRowCount = dataRowCount - successCount;
        Path report = Path.of("target", "gonnyu-lac-pinyin-failures.tsv")
                .toAbsolutePath().normalize();
        writeReport(report, failures);
        System.out.printf("数据文件：%s%n", source);
        System.out.printf("总行数：%d，解析成功：%d，解析失败：%d，失败拼音种类：%d%n%n",
                dataRowCount, successCount, failedRowCount, failures.size());
        System.out.printf("失败清单：%s%n", report);
    }

    private static boolean canParse(String pinyin)
    {
        try
        {
            return !pinyin.isEmpty() && LACPinyin.tryOf(KeyboardPinyin.of(pinyin)).isValid();
        }
        catch (RuntimeException ignored)
        {
            return false;
        }
    }

    /**
     * GonnyuGeneralIME 与豫章词的元音记法不同：eo 对应 oe，ê 对应 e，e 对应 ee。
     * 按字符一次性转换，避免转换结果再次参与替换。
     */
    private static String normalizeGonnyuPinyin(String pinyin)
    {
        StringBuilder normalized = new StringBuilder(pinyin.length() + 2);
        for (int index = 0; index < pinyin.length(); index++)
        {
            char current = pinyin.charAt(index);
            if (current == 'e' && index + 1 < pinyin.length() && pinyin.charAt(index + 1) == 'o')
            {
                normalized.append("oe");
                index++;
            }
            else if (current == 'ê') normalized.append('e');
            else if (current == 'e') normalized.append("ee");
            else normalized.append(current);
        }
        return normalized.toString();
    }

    private static Path sourcePath()
    {
        String configured = System.getProperty("gonnyu.chars");
        Path path = configured == null || configured.isBlank()
                ? DEFAULT_SOURCE
                : Path.of(configured);
        path = path.toAbsolutePath().normalize();
        if (!Files.isRegularFile(path))
            throw new IllegalStateException("找不到 Gonnyu 字表：" + path);
        return path;
    }

    private static String[] splitTsv(String line)
    {
        return line.split("\\t", -1);
    }

    private static int findColumn(String[] headers, String name)
    {
        for (int index = 0; index < headers.length; index++)
            if (name.equals(headers[index])) return index;
        throw new IllegalStateException("TSV 缺少“" + name + "”列");
    }

    private static String column(String[] columns, int index)
    {
        return index < columns.length ? columns[index] : "";
    }

    private static String removeBom(String text)
    {
        return text.startsWith("\uFEFF") ? text.substring(1) : text;
    }

    private static String printable(String pinyin)
    {
        return pinyin.isEmpty() ? "<空>" : pinyin;
    }

    private static void writeReport(Path report, Map<FailureKey, Failure> failures) throws IOException
    {
        StringBuilder text = new StringBuilder("原始拼音\t转换后拼音\t失败行数\t行号（最多8个）\t示例字（最多8个）\n");
        failures.forEach((key, failure) -> text
                .append(printable(key.original())).append('\t')
                .append(printable(key.normalized())).append('\t')
                .append(failure.count).append('\t')
                .append(failure.lineNumbers).append('\t')
                .append(failure.words).append('\n'));
        Files.createDirectories(report.getParent());
        Files.writeString(report, text, StandardCharsets.UTF_8);
    }

    private record FailureKey(String original, String normalized) {}

    private static final class Failure
    {
        private static final int EXAMPLE_LIMIT = 8;
        private int count;
        private final List<Integer> lineNumbers = new ArrayList<>();
        private final List<String> words = new ArrayList<>();

        private void add(int lineNumber, String word)
        {
            count++;
            if (lineNumbers.size() < EXAMPLE_LIMIT) lineNumbers.add(lineNumber);
            if (words.size() < EXAMPLE_LIMIT && !word.isBlank() && !words.contains(word)) words.add(word);
        }
    }
}
