package com.shuowen.yuzong.reference.tsv;

import com.shuowen.yuzong.linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.linguistics.pinyinv2.lac.LACPinyin;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import com.shuowen.yuzong.util.text.ScTcCharacterConverter;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReferenceTsvClassificationService
{
    private final ReferenceTsvService referenceTsvService;
    private static final List<String> COLUMNS = List.of("本词", "国际音标", "方言拼音", "汉语拼音", "词汇属性",
            "对应官话词", "官话拼音", "词频", "同义词", "新旧标记");

    public Result classify(String id) throws IOException
    {
        List<ReferenceTsvService.Row> rows = referenceTsvService.readAllRows(id);
        Map<String, List<String[]>> groups = new LinkedHashMap<>();
        for (ReferenceTsvService.Row row : rows)
            groups.computeIfAbsent(row.values()[0], ignored -> new ArrayList<>()).add(row.values());

        Map<String, List<String>> reasonsByCharacter = new HashMap<>();
        Map<String, List<String>> mandarinByCharacter = new HashMap<>();
        Map<String, List<String>> formRelationsByCharacter = new HashMap<>();
        for (var entry : groups.entrySet())
        {
            reasonsByCharacter.put(entry.getKey(), reasons(entry.getKey(), entry.getValue()));
            mandarinByCharacter.put(entry.getKey(), mandarinReadings(entry.getKey()));
            if (entry.getKey().codePointCount(0, entry.getKey().length()) == 1)
            {
                var forms = ScTcCharacterConverter.lookup(entry.getKey());
                if (!isOneToOne(forms)) formRelationsByCharacter.put(entry.getKey(), formRelations(forms));
            }
        }
        int generalCount = (int) reasonsByCharacter.values().stream().filter(List::isEmpty).count();

        List<ClassifiedRow> classified = new ArrayList<>(rows.size());
        for (ReferenceTsvService.Row row : rows)
        {
            List<String> reasons = reasonsByCharacter.get(row.values()[0]);
            boolean general = reasons.isEmpty();
            classified.add(new ClassifiedRow(row.line(), row.values(), general, reasons,
                    mandarinByCharacter.get(row.values()[0]),
                    formRelationsByCharacter.getOrDefault(row.values()[0], List.of()),
                    displayDialect(row.values()[2])));
        }
        return new Result(COLUMNS, classified, generalCount, groups.size() - generalCount);
    }

    private String displayDialect(String pinyin)
    {
        try
        {
            String standard = pinyin.trim().toLowerCase(Locale.ROOT).replace("eo", "ọ").replace("oe", "ọ");
            return LACPinyin.tryOf(KeyboardPinyin.of(standard), LACPinyin.KeyboardScheme.STANDARD)
                    .getValueDirectly("TSV 中的方言拼音无效：" + pinyin).toRPinyin().toHandleString();
        }
        catch (RuntimeException ignored)
        {
            return pinyin;
        }
    }

    private List<String> reasons(String character, List<String[]> rows)
    {
        List<String> reasons = new ArrayList<>();
        if (character.codePointCount(0, character.length()) != 1)
        {
            reasons.add("不是单字");
        }
        else
        {
            var forms = ScTcCharacterConverter.lookup(character);
            if (!isOneToOne(forms)) reasons.add("简繁非一一对应");

            if (mandarinReadings(character).isEmpty()) reasons.add("普通话读音未收录");
        }

        Set<String> nanchang = new HashSet<>();
        for (String[] row : rows) nanchang.add(row[2].trim());
        if (mandarinReadings(character).size() > 1 || nanchang.size() > 1)
            reasons.add("多音字");
        if (rows.stream().anyMatch(row -> !row[8].isBlank())) reasons.add("存在同义词");
        if (rows.stream().anyMatch(row -> !row[9].isBlank())) reasons.add("存在新旧标记");
        return List.copyOf(reasons);
    }

    private List<String> mandarinReadings(String character)
    {
        if (character.codePointCount(0, character.length()) != 1) return List.of();
        var forms = ScTcCharacterConverter.lookup(character);
        Set<String> shapes = new HashSet<>();
        shapes.add(character);
        forms.simplified().forEach(ch -> shapes.add(ch.toString()));
        forms.traditional().forEach(ch -> shapes.add(ch.toString()));
        Set<String> readings = new java.util.TreeSet<>();
        for (String shape : shapes)
            for (HanPinyin pronunciation : HanPinyin.toPinyinList(shape))
                readings.add(pronunciation.getSyll() + pronunciation.getTone());
        return List.copyOf(readings);
    }

    private boolean isOneToOne(ScTcCharacterConverter.Forms forms)
    {
        if (forms.simplified().size() != 1 || forms.traditional().size() != 1) return false;
        String simplified = forms.simplified().get(0).toString();
        String traditional = forms.traditional().get(0).toString();
        var fromSimplified = ScTcCharacterConverter.lookup(simplified);
        var fromTraditional = ScTcCharacterConverter.lookup(traditional);
        return fromSimplified.simplified().equals(forms.simplified())
                && fromSimplified.traditional().equals(forms.traditional())
                && fromTraditional.simplified().equals(forms.simplified())
                && fromTraditional.traditional().equals(forms.traditional());
    }

    private List<String> formRelations(ScTcCharacterConverter.Forms forms)
    {
        Set<String> details = new java.util.LinkedHashSet<>();
        for (var simplified : forms.simplified())
        {
            var related = ScTcCharacterConverter.lookup(simplified.toString());
            details.add("简体 " + simplified + " → 繁体 " +
                    related.traditional().stream().map(Object::toString).collect(java.util.stream.Collectors.joining("、")));
        }
        for (var traditional : forms.traditional())
        {
            var related = ScTcCharacterConverter.lookup(traditional.toString());
            details.add("繁体 " + traditional + " → 简体 " +
                    related.simplified().stream().map(Object::toString).collect(java.util.stream.Collectors.joining("、")));
        }
        return List.copyOf(details);
    }

    public record ClassifiedRow(int line, String[] values, boolean general, List<String> reasons,
                                List<String> mandarinReadings, List<String> formRelations, String dialectDisplay) {}
    public record Result(List<String> columns, List<ClassifiedRow> rows, int generalCount, int specialCount) {}
}
