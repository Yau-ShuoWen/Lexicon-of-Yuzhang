package com.shuowen.yuzong.reference.tsv;

import com.shuowen.yuzong.linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.util.text.ScTcCharacterConverter;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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
        for (var entry : groups.entrySet()) reasonsByCharacter.put(entry.getKey(), reasons(entry.getKey(), entry.getValue()));
        int generalCount = (int) reasonsByCharacter.values().stream().filter(List::isEmpty).count();

        List<ClassifiedRow> classified = new ArrayList<>(rows.size());
        for (ReferenceTsvService.Row row : rows)
        {
            List<String> reasons = reasonsByCharacter.get(row.values()[0]);
            boolean general = reasons.isEmpty();
            classified.add(new ClassifiedRow(row.line(), row.values(), general, reasons));
        }
        return new Result(COLUMNS, classified, generalCount, groups.size() - generalCount);
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

            Set<String> mandarin = new HashSet<>();
            Set<String> shapes = new HashSet<>();
            shapes.add(character);
            forms.simplified().forEach(ch -> shapes.add(ch.toString()));
            forms.traditional().forEach(ch -> shapes.add(ch.toString()));
            for (String shape : shapes)
                for (HanPinyin pronunciation : HanPinyin.toPinyinList(shape))
                    mandarin.add(pronunciation.getSyll() + pronunciation.getTone());
            if (mandarin.size() > 1) reasons.add("普通话多音");
            else if (mandarin.isEmpty()) reasons.add("普通话读音未收录");
        }

        Set<String> nanchang = new HashSet<>();
        for (String[] row : rows) nanchang.add(row[2].trim());
        if (nanchang.size() > 1) reasons.add("南昌话多音");
        if (rows.stream().anyMatch(row -> !row[8].isBlank())) reasons.add("存在同义词");
        if (rows.stream().anyMatch(row -> !row[9].isBlank())) reasons.add("存在新旧标记");
        return List.copyOf(reasons);
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

    public record ClassifiedRow(int line, String[] values, boolean general, List<String> reasons) {}
    public record Result(List<String> columns, List<ClassifiedRow> rows, int generalCount, int specialCount) {}
}
