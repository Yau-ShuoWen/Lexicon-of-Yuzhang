package com.shuowen.yuzong.migration;

import com.shuowen.yuzong.dict.data.domain.IPA.IPASyllStyle;
import com.shuowen.yuzong.dict.data.domain.IPA.IPAToneStyle;
import com.shuowen.yuzong.linguistics.pinyinv2.lac.LACPinyin;
import com.shuowen.yuzong.linguistics.pinyinv2.lac.Dict_NanchangFangyanCidian;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检查 GonnyuGeneralIME 南昌话字表中的拼音能否被豫章词解析。
 *
 * <p>本工具只读源 TSV，不修改或清洗任何数据。等确认替换原则后，再在解析前加入显式转换。</p>
 */
class GonnyuLacPinyinAuditTest
{
    private static final Pattern TRAILING_DIGITS = Pattern.compile("(\\d+)$");
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

    @Test
    void reportIpaSyllableDifferences() throws IOException
    {
        Path source = sourcePath();
        Map<IpaDifferenceKey, Failure> differences = new LinkedHashMap<>();
        Map<IpaComparisonKey, Failure> comparisons = new LinkedHashMap<>();
        int dataRowCount = 0;
        int matchCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8))
        {
            String[] headers = readHeaders(reader, source);
            int wordColumn = findColumn(headers, "本词");
            int ipaColumn = findColumn(headers, "国际音标");
            int pinyinColumn = findColumn(headers, "方言拼音");

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                dataRowCount++;
                String[] columns = splitTsv(line);
                String word = column(columns, wordColumn);
                String sourceIpa = removeTrailingDigits(column(columns, ipaColumn).trim());
                String originalPinyin = column(columns, pinyinColumn).trim();
                String normalizedPinyin = removeTrailingDigits(normalizeGonnyuPinyin(originalPinyin));
                LACPinyin pinyin = LACPinyin.tryOf(KeyboardPinyin.of(normalizedPinyin))
                        .getValueDirectly("已通过拼音审计却无法解析：" + originalPinyin);
                var generated = Dict_NanchangFangyanCidian.INSTANCE.transcribe(
                        pinyin, IPASyllStyle.CHINESE_SPECIAL, IPAToneStyle.FIVE_DEGREE_NUM);
                String generatedIpa = generated.isValid()
                        ? generated.getValue().value().replace("'", "ʰ")
                        : "<词典未收录>";
                boolean ipaMatches = ipaEquals(sourceIpa, generatedIpa);
                String status = !generated.isValid()
                        ? "未收录"
                        : ipaMatches ? "一致" : "不一致";
                comparisons.computeIfAbsent(
                                new IpaComparisonKey(
                                        removeTrailingDigits(originalPinyin), normalizedPinyin,
                                        sourceIpa, generatedIpa, status),
                                ignored -> new Failure())
                        .add(lineNumber, word);

                if (ipaMatches)
                {
                    matchCount++;
                    continue;
                }
                differences.computeIfAbsent(
                                new IpaDifferenceKey(originalPinyin, sourceIpa, generatedIpa),
                                ignored -> new Failure())
                        .add(lineNumber, word);
            }
        }

        Path report = Path.of("target", "gonnyu-lac-ipa-syllable-differences.tsv")
                .toAbsolutePath().normalize();
        Path comparisonReport = Path.of("target", "gonnyu-lac-ipa-comparison.tsv")
                .toAbsolutePath().normalize();
        writeIpaReport(report, differences);
        writeIpaComparisonReport(comparisonReport, comparisons);
        System.out.printf("IPA 音节总行数：%d，一致：%d，不一致：%d，差异种类：%d%n",
                dataRowCount, matchCount, dataRowCount - matchCount, differences.size());
        System.out.printf("IPA 音节差异清单：%s%n", report);
        System.out.printf("IPA 完整并列表：%s%n", comparisonReport);
    }

    @Test
    void reportToneCorrespondences() throws IOException
    {
        Path source = sourcePath();
        Map<ToneKey, Failure> correspondences = new LinkedHashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8))
        {
            String[] headers = readHeaders(reader, source);
            int wordColumn = findColumn(headers, "本词");
            int ipaColumn = findColumn(headers, "国际音标");
            int pinyinColumn = findColumn(headers, "方言拼音");

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                String[] columns = splitTsv(line);
                String word = column(columns, wordColumn);
                String ipa = column(columns, ipaColumn).trim();
                String pinyin = normalizeGonnyuPinyin(column(columns, pinyinColumn).trim());
                correspondences.computeIfAbsent(
                                new ToneKey(trailingDigits(ipa), trailingDigits(pinyin)),
                                ignored -> new Failure())
                        .add(lineNumber, word);
            }
        }

        Path report = Path.of("target", "gonnyu-lac-tone-correspondences.tsv")
                .toAbsolutePath().normalize();
        writeToneReport(report, correspondences);
        System.out.printf("音调对应关系：%d 种，报告：%s%n", correspondences.size(), report);
    }

    @Test
    void writeReorderedComparisonTsv() throws IOException
    {
        Path source = sourcePath();
        Path output = source.resolveSibling("chars-yuzhang.tsv");
        StringBuilder text = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8))
        {
            String[] headers = readHeaders(reader, source);
            int wordColumn = findColumn(headers, "本词");
            int ipaColumn = findColumn(headers, "国际音标");
            int pinyinColumn = findColumn(headers, "方言拼音");
            List<String> outputHeaders = new ArrayList<>(List.of(
                    "本词", "（原）方言拼音", "豫章词拼音", "拼音差异",
                    "（原）国际音标", "南昌方言词典音标", "国际音标差异"
            ));
            for (int index = 0; index < headers.length; index++)
                if (index != wordColumn && index != ipaColumn && index != pinyinColumn)
                    outputHeaders.add(headers[index]);
            appendTsvRow(text, outputHeaders);

            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] columns = splitTsv(line);
                String originalPinyin = column(columns, pinyinColumn).trim();
                String yuzhangPinyin = toProjectKeyboardPinyin(
                        normalizeAndCorrectPinyin(originalPinyin));
                String originalIpa = column(columns, ipaColumn).trim();
                String sourceSyllable = removeTrailingDigits(originalIpa);
                String generatedIpa = generateUntonedIpa(yuzhangPinyin);
                List<String> outputRow = new ArrayList<>(List.of(
                        column(columns, wordColumn), originalPinyin, yuzhangPinyin,
                        describePinyinDifference(originalPinyin), originalIpa, generatedIpa,
                        describeIpaDifference(sourceSyllable, generatedIpa)
                ));
                for (int index = 0; index < headers.length; index++)
                    if (index != wordColumn && index != ipaColumn && index != pinyinColumn)
                        outputRow.add(column(columns, index));
                appendTsvRow(text, outputRow);
            }
        }

        Files.createDirectories(output.getParent());
        Files.writeString(output, text, StandardCharsets.UTF_8);
        System.out.printf("重排后的 Gonnyu 对照表：%s%n", output);
    }

    private static void appendTsvRow(StringBuilder output, List<String> fields)
    {
        for (int index = 0; index < fields.size(); index++)
        {
            if (index > 0) output.append('\t');
            output.append('"').append(fields.get(index).replace("\"", "\"\"")).append('"');
        }
        output.append('\n');
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

    private static String[] readHeaders(BufferedReader reader, Path source) throws IOException
    {
        String headerLine = reader.readLine();
        if (headerLine == null) throw new IllegalStateException("TSV 文件为空：" + source);
        return splitTsv(removeBom(headerLine));
    }

    private static String removeTrailingDigits(String text)
    {
        return TRAILING_DIGITS.matcher(text).replaceFirst("");
    }

    private static String trailingDigits(String text)
    {
        Matcher matcher = TRAILING_DIGITS.matcher(text);
        return matcher.find() ? matcher.group(1) : "<无>";
    }

    private static boolean ipaEquals(String sourceIpa, String generatedIpa)
    {
        return canonicalIpa(sourceIpa).equals(canonicalIpa(generatedIpa));
    }

    /** 比较时视为同一音标；报告仍保留来源中的原始写法。 */
    private static String canonicalIpa(String ipa)
    {
        String canonical = ipa
                .replace("ɔː", "ɔ")
                .replace('ɑ', 'a')
                .replace('ɛ', 'ε')
                .replace('e', 'ε')
                .replace('ə', 'ɨ')
                .replace('o', 'ɔ')
                .replace('ɸ', 'f')
                .replace('ᴀ', 'a')
                .replace('ʊ', 'u')
                .replace("'", "ʰ");
        return canonical.equals("m") ? "m̩" : canonical;
    }

    private static String normalizeAndCorrectPinyin(String originalPinyin)
    {
        String originalTone = trailingDigits(originalPinyin);
        if ("<无>".equals(originalTone)) originalTone = "";
        if (removeTrailingDigits(originalPinyin).equals("net")) return "niet" + originalTone;

        String normalized = normalizeGonnyuPinyin(originalPinyin);
        String tone = trailingDigits(normalized);
        if ("<无>".equals(tone)) tone = "";
        String syllable = removeTrailingDigits(normalized);
        syllable = switch (syllable)
        {
            case "yung" -> "iung";
            case "xyung" -> "xiung";
            case "nen" -> "neen";
            case "net" -> "niet";
            default -> syllable;
        };
        return syllable + tone;
    }

    private static String generateUntonedIpa(String yuzhangPinyin)
    {
        String syllable = removeTrailingDigits(yuzhangPinyin);
        var parsed = LACPinyin.tryOf(KeyboardPinyin.of(syllable));
        if (parsed.isEmpty()) return "<拼音无法解析>";
        var generated = Dict_NanchangFangyanCidian.INSTANCE.transcribe(
                parsed.getValue(), IPASyllStyle.CHINESE_SPECIAL, IPAToneStyle.FIVE_DEGREE_NUM);
        return generated.isValid() ? generated.getValue().value() : "<词典未收录>";
    }

    private static String toProjectKeyboardPinyin(String pinyin)
    {
        return LACPinyin.tryOf(KeyboardPinyin.of(pinyin))
                .getValueDirectly("拼音无法转为豫章词 keyboard 版本：" + pinyin)
                .toKeyboardPinyin()
                .toString();
    }

    private static String describePinyinDifference(String originalPinyin)
    {
        String syllable = removeTrailingDigits(originalPinyin);
        List<String> rules = new ArrayList<>();
        if (syllable.contains("eo")) rules.add("oe=eo");
        if (syllable.contains("ê")) rules.add("ê=e");
        if (syllable.replace("eo", "").contains("e")) rules.add("e=ẹ");
        return rules.isEmpty() ? "✓" : String.join("；", rules);
    }

    private static String describeIpaDifference(String source, String generated)
    {
        if (generated.startsWith("<")) return generated + "，暂时保留原国际音标。";
        if (source.equals(generated)) return "✓";
        if (ipaEquals(source, generated))
            return String.join("；", appliedEquivalenceRules(source, generated));
        return "仍有差异，需要人工确认：" + source + " ≠ " + generated + "。";
    }

    private static List<String> appliedEquivalenceRules(String source, String generated)
    {
        List<String> rules = new ArrayList<>();
        addRuleIfApplied(rules, source, generated, "ɛ", "ε");
        addRuleIfApplied(rules, source, generated, "e", "ε");
        addRuleIfApplied(rules, source, generated, "ə", "ɨ");
        addRuleIfApplied(rules, source, generated, "ɔː", "ɔ");
        addRuleIfApplied(rules, source, generated, "o", "ɔ");
        addRuleIfApplied(rules, source, generated, "ɸ", "f");
        addRuleIfApplied(rules, source, generated, "ᴀ", "a");
        addRuleIfApplied(rules, source, generated, "ɑ", "a");
        addRuleIfApplied(rules, source, generated, "ʊ", "u");
        if ((source.equals("m") && generated.equals("m̩")) ||
                (source.equals("m̩") && generated.equals("m")))
            rules.add("m=m̩");
        addRuleIfApplied(rules, source, generated, "'", "ʰ");
        return rules;
    }

    private static void addRuleIfApplied(
            List<String> rules,
            String source,
            String generated,
            String first,
            String second
    )
    {
        if ((source.contains(first) && generated.contains(second)) ||
                (source.contains(second) && generated.contains(first)))
            rules.add(first + "=" + second);
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

    private static void writeIpaReport(Path report, Map<IpaDifferenceKey, Failure> differences)
            throws IOException
    {
        StringBuilder text = new StringBuilder(
                "原始拼音\tTSV音节\t词典生成音节\t行数\t行号（最多8个）\t示例字（最多8个）\n");
        differences.forEach((key, failure) -> text
                .append(key.pinyin()).append('\t')
                .append(key.sourceIpa()).append('\t')
                .append(key.generatedIpa()).append('\t')
                .append(failure.count).append('\t')
                .append(failure.lineNumbers).append('\t')
                .append(failure.words).append('\n'));
        Files.createDirectories(report.getParent());
        Files.writeString(report, text, StandardCharsets.UTF_8);
    }

    private static void writeToneReport(Path report, Map<ToneKey, Failure> correspondences)
            throws IOException
    {
        StringBuilder text = new StringBuilder(
                "TSV国际音标末尾数字\t项目拼音音调\t行数\t行号（最多8个）\t示例字（最多8个）\n");
        correspondences.forEach((key, failure) -> text
                .append(key.ipaTone()).append('\t')
                .append(key.pinyinTone()).append('\t')
                .append(failure.count).append('\t')
                .append(failure.lineNumbers).append('\t')
                .append(failure.words).append('\n'));
        Files.createDirectories(report.getParent());
        Files.writeString(report, text, StandardCharsets.UTF_8);
    }

    private static void writeIpaComparisonReport(
            Path report,
            Map<IpaComparisonKey, Failure> comparisons
    ) throws IOException
    {
        StringBuilder text = new StringBuilder(
                "TSV无调拼音\t项目无调拼音\tTSV无调IPA\t词典生成IPA\t状态\t行数\t行号（最多8个）\t示例字（最多8个）\n");
        comparisons.forEach((key, value) -> text
                .append(key.originalPinyin()).append('\t')
                .append(key.normalizedPinyin()).append('\t')
                .append(key.sourceIpa()).append('\t')
                .append(key.generatedIpa()).append('\t')
                .append(key.status()).append('\t')
                .append(value.count).append('\t')
                .append(value.lineNumbers).append('\t')
                .append(value.words).append('\n'));
        Files.createDirectories(report.getParent());
        Files.writeString(report, text, StandardCharsets.UTF_8);
    }

    private record FailureKey(String original, String normalized) {}

    private record IpaDifferenceKey(String pinyin, String sourceIpa, String generatedIpa) {}

    private record IpaComparisonKey(
            String originalPinyin,
            String normalizedPinyin,
            String sourceIpa,
            String generatedIpa,
            String status
    ) {}

    private record ToneKey(String ipaTone, String pinyinTone) {}

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
