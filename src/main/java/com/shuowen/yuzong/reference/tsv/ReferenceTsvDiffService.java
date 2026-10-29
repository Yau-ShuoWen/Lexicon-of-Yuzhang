package com.shuowen.yuzong.reference.tsv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReferenceTsvDiffService
{
    private static final List<String> COLUMNS = List.of("本词", "国际音标", "方言拼音", "汉语拼音", "词汇属性",
            "对应官话词", "官话拼音", "词频", "同义词", "新旧标记");
    private static final List<String> TYPES = List.of("unchanged", "modified", "possible", "conflict", "added", "removed");
    private final ReferenceTsvService tsvService;

    public DiffPage compare(String fromId, String toId, String type, String query, int page, int size) throws IOException
    {
        if (fromId.equals(toId)) throw new IllegalArgumentException("请选择两个不同版本");
        if (!type.equals("changes") && !type.equals("all") && !TYPES.contains(type))
            throw new IllegalArgumentException("差异类型无效");
        if (page < 1 || size < 1 || size > 100) throw new IllegalArgumentException("分页参数无效");
        List<ReferenceTsvService.Row> before = tsvService.readAllRows(fromId);
        List<ReferenceTsvService.Row> after = tsvService.readAllRows(toId);
        List<DiffItem> items = build(before, after);
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String category : TYPES) counts.put(category, 0);
        for (DiffItem item : items) counts.merge(item.type(), 1, Integer::sum);

        String term = query == null ? "" : query.trim();
        List<DiffItem> filtered = items.stream()
                .filter(item -> type.equals("all") || type.equals("changes") && !item.type().equals("unchanged")
                        || item.type().equals(type))
                .filter(item -> term.isEmpty() || contains(item, term))
                .toList();
        long start = (long) (page - 1) * size;
        int from = (int) Math.min(start, filtered.size());
        int to = (int) Math.min(start + size, filtered.size());
        return new DiffPage(COLUMNS, counts, filtered.subList(from, to), filtered.size(), page, size);
    }

    private List<DiffItem> build(List<ReferenceTsvService.Row> before, List<ReferenceTsvService.Row> after)
    {
        List<DiffItem> items = new ArrayList<>();
        Set<Integer> usedBefore = new HashSet<>();
        Set<Integer> usedAfter = new HashSet<>();

        // 重复的整行按出现次序一对一抵消，不能把同字多音合并。
        Map<List<String>, ArrayDeque<ReferenceTsvService.Row>> exact = new HashMap<>();
        for (var row : before) exact.computeIfAbsent(Arrays.asList(row.values()), ignored -> new ArrayDeque<>()).add(row);
        for (var row : after)
        {
            var candidates = exact.get(Arrays.asList(row.values()));
            if (candidates == null || candidates.isEmpty()) continue;
            var previous = candidates.removeFirst();
            usedBefore.add(previous.line());
            usedAfter.add(row.line());
            items.add(new DiffItem("unchanged", List.of(previous), List.of(row), List.of()));
        }

        // 只在两边都唯一时，才把同一字、同一读音和属性判为确定对应。
        Map<List<String>, List<ReferenceTsvService.Row>> beforeKeys = groupedByKey(before, usedBefore);
        Map<List<String>, List<ReferenceTsvService.Row>> afterKeys = groupedByKey(after, usedAfter);
        for (var entry : beforeKeys.entrySet())
        {
            var oldRows = entry.getValue();
            var newRows = afterKeys.get(entry.getKey());
            if (oldRows.size() != 1 || newRows == null || newRows.size() != 1) continue;
            var oldRow = oldRows.get(0);
            var newRow = newRows.get(0);
            usedBefore.add(oldRow.line());
            usedAfter.add(newRow.line());
            items.add(new DiffItem("modified", List.of(oldRow), List.of(newRow), changedColumns(oldRow, newRow)));
        }

        Map<String, List<ReferenceTsvService.Row>> oldWords = groupedByWord(before, usedBefore);
        Map<String, List<ReferenceTsvService.Row>> newWords = groupedByWord(after, usedAfter);
        Set<String> words = new java.util.LinkedHashSet<>(oldWords.keySet());
        words.addAll(newWords.keySet());
        for (String word : words)
        {
            List<ReferenceTsvService.Row> oldRows = oldWords.getOrDefault(word, List.of());
            List<ReferenceTsvService.Row> newRows = newWords.getOrDefault(word, List.of());
            if (oldRows.size() == 1 && newRows.size() == 1)
                items.add(new DiffItem("possible", oldRows, newRows, changedColumns(oldRows.get(0), newRows.get(0))));
            else if (!oldRows.isEmpty() && !newRows.isEmpty())
                items.add(new DiffItem("conflict", oldRows, newRows, List.of()));
            else
            {
                for (var row : oldRows) items.add(new DiffItem("removed", List.of(row), List.of(), List.of()));
                for (var row : newRows) items.add(new DiffItem("added", List.of(), List.of(row), List.of()));
            }
        }
        return items;
    }

    private static Map<List<String>, List<ReferenceTsvService.Row>> groupedByKey(
            List<ReferenceTsvService.Row> rows, Set<Integer> used)
    {
        Map<List<String>, List<ReferenceTsvService.Row>> groups = new LinkedHashMap<>();
        for (var row : rows)
        {
            if (used.contains(row.line())) continue;
            String[] values = row.values();
            List<String> key = List.of(values[0], values[1], values[2], values[4]);
            groups.computeIfAbsent(key, ignored -> new ArrayList<>()).add(row);
        }
        return groups;
    }

    private static Map<String, List<ReferenceTsvService.Row>> groupedByWord(
            List<ReferenceTsvService.Row> rows, Set<Integer> used)
    {
        Map<String, List<ReferenceTsvService.Row>> groups = new LinkedHashMap<>();
        for (var row : rows)
            if (!used.contains(row.line())) groups.computeIfAbsent(row.values()[0], ignored -> new ArrayList<>()).add(row);
        return groups;
    }

    private static List<String> changedColumns(ReferenceTsvService.Row before, ReferenceTsvService.Row after)
    {
        List<String> changed = new ArrayList<>();
        for (int i = 0; i < COLUMNS.size(); i++)
            if (!before.values()[i].equals(after.values()[i])) changed.add(COLUMNS.get(i));
        return changed;
    }

    private static boolean contains(DiffItem item, String term)
    {
        for (var row : item.before()) for (String value : row.values()) if (value.contains(term)) return true;
        for (var row : item.after()) for (String value : row.values()) if (value.contains(term)) return true;
        return false;
    }

    public record DiffItem(String type, List<ReferenceTsvService.Row> before,
                           List<ReferenceTsvService.Row> after, List<String> changedColumns) {}
    public record DiffPage(List<String> columns, Map<String, Integer> counts,
                           List<DiffItem> items, int total, int page, int size) {}
}
