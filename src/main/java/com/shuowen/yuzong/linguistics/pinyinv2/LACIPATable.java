package com.shuowen.yuzong.linguistics.pinyinv2;

import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.tuple.Maybe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 读取随代码发布的南昌话国际音标资料表。
 * CSV 中空单元格表示合法的空音值，{@code -} 表示对应资料没有收录。
 */
final class LACIPATable
{
    private static final String RESOURCE = "/linguistics/lac-ipa.csv";
    private static final String[] HEADER = {
            "type", "standard", "style", "ncdict", "gansum", "cndialdict",
            "ncrecord", "ncstudy", "ncphon", "ncphon_y"
    };
    private static final Set<String> COMPONENT_TYPES = Set.of("dandu", "initial", "final");
    private static final Set<String> TONE_STYLES = Set.of(
            "five_degree_line", "five_degree_num", "four_corner"
    );
    private static final Map<DictCode, Integer> DICT_COLUMNS = createDictColumns();
    private static final Map<Key, Row> ROWS = load();

    private LACIPATable()
    {
    }

    static Maybe<String> find(String type, String standard, String style, DictCode dict)
    {
        Row row = ROWS.get(new Key(type, standard, style));
        if (row == null)
            throw new InvalidPinyinException(
                    "国际音标表没有记录南昌话" + type + "：" + standard + "，格式：" + style
            );

        String ipa = row.ipa().get(dict);
        if (ipa == null)
            throw new IllegalArgumentException("国际音标表不支持资料：" + dict);
        return ipa.equals("-") ? Maybe.nothing() : Maybe.exist(ipa);
    }

    private static Map<DictCode, Integer> createDictColumns()
    {
        Map<DictCode, Integer> columns = new EnumMap<>(DictCode.class);
        columns.put(DictCode.NCDICT, 3);
        columns.put(DictCode.GANSUM, 4);
        columns.put(DictCode.CNDIALDICT, 5);
        columns.put(DictCode.NCRECORD, 6);
        columns.put(DictCode.NCSTUDY, 7);
        columns.put(DictCode.NCPHON, 8);
        columns.put(DictCode.NCPHON_Y, 9);
        return Map.copyOf(columns);
    }

    private static Map<Key, Row> load()
    {
        InputStream stream = LACIPATable.class.getResourceAsStream(RESOURCE);
        if (stream == null) throw new IllegalStateException("找不到南昌话国际音标表：" + RESOURCE);

        Map<Key, Row> rows = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
        {
            checkHeader(reader.readLine());
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null)
            {
                lineNumber++;
                if (line.isBlank()) continue;
                String[] columns = line.split(",", -1);
                if (columns.length != HEADER.length)
                    throw new IllegalStateException("南昌话国际音标表第" + lineNumber + "行列数错误");

                validateRow(columns, lineNumber);
                Key key = new Key(columns[0], columns[1], columns[2]);
                Map<DictCode, String> ipa = new EnumMap<>(DictCode.class);
                DICT_COLUMNS.forEach((dict, index) -> ipa.put(dict, columns[index]));
                Row previous = rows.put(key, new Row(Map.copyOf(ipa)));
                if (previous != null)
                    throw new IllegalStateException("南昌话国际音标表存在重复项目：" + key);
            }
        } catch (IOException e)
        {
            throw new IllegalStateException("读取南昌话国际音标表失败", e);
        }
        checkRequiredRows(rows);
        return Map.copyOf(rows);
    }

    private static void checkRequiredRows(Map<Key, Row> rows)
    {
        for (LACPinyin.DanDu dandu : LACPinyin.DanDu.values())
            requireRow(rows, new Key("dandu", dandu.toString(), ""));
        for (LACPinyin.ShengMu shengmu : LACPinyin.ShengMu.values())
            requireRow(rows, new Key("initial", shengmu.name, ""));
        for (int tone = 0; tone <= 7; tone++)
            for (String style : TONE_STYLES)
                requireRow(rows, new Key("tone", Integer.toString(tone), style));
    }

    private static void requireRow(Map<Key, Row> rows, Key key)
    {
        if (!rows.containsKey(key))
            throw new IllegalStateException("南昌话国际音标表缺少必要项目：" + key);
    }

    private static void validateRow(String[] columns, int lineNumber)
    {
        String type = columns[0];
        String standard = columns[1];
        String style = columns[2];
        boolean tone = type.equals("tone");

        if (!tone && !COMPONENT_TYPES.contains(type))
            throw new IllegalStateException("南昌话国际音标表第" + lineNumber + "行类型无效：" + type);
        if (tone && !TONE_STYLES.contains(style))
            throw new IllegalStateException("南昌话国际音标表第" + lineNumber + "行声调格式无效：" + style);
        if (!tone && !style.isEmpty())
            throw new IllegalStateException("南昌话国际音标表第" + lineNumber + "行的音节数据不应指定声调格式");
        if (tone && !standard.matches("[0-7]"))
            throw new IllegalStateException("南昌话国际音标表第" + lineNumber + "行声调无效：" + standard);

        for (int column : DICT_COLUMNS.values())
        {
            String value = columns[column];
            if (tone && !value.equals("-") && !value.contains("{syllable}"))
                throw new IllegalStateException("南昌话国际音标表第" + lineNumber + "行声调模板缺少{syllable}");
            if (!tone && value.contains("{syllable}"))
                throw new IllegalStateException("南昌话国际音标表第" + lineNumber + "行音节数据不应包含模板");
        }
    }

    private static void checkHeader(String line)
    {
        if (line == null) throw new IllegalStateException("南昌话国际音标表为空");
        String[] columns = line.split(",", -1);
        if (columns.length != HEADER.length)
            throw new IllegalStateException("南昌话国际音标表表头列数错误");
        for (int i = 0; i < HEADER.length; i++)
            if (!HEADER[i].equals(columns[i]))
                throw new IllegalStateException("南昌话国际音标表第" + (i + 1) + "列表头应为：" + HEADER[i]);
    }

    private record Key(String type, String standard, String style) {}

    private record Row(Map<DictCode, String> ipa) {}
}
