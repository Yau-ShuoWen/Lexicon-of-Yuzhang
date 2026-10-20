package com.shuowen.yuzong.reference.tsv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.dict.hanzi.domain.HanziPronunciation;
import com.shuowen.yuzong.dict.hanzi.domain.MdrTool;
import com.shuowen.yuzong.dict.hanzi.mapper.HanziMapper;
import com.shuowen.yuzong.dict.hanzi.mapper.PronunMapper;
import com.shuowen.yuzong.dict.hanzi.model.HanziEntity;
import com.shuowen.yuzong.dict.hanzi.model.MdrChar;
import com.shuowen.yuzong.linguistics.Mandarin.HanPinyin;
import com.shuowen.yuzong.linguistics.pinyinv2.lac.LACPinyin;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcCharacterConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReferenceTsvAuditService
{
    private static final Dialect DIALECT = Dialect.LAC;

    private final ReferenceTsvService referenceTsvService;
    private final HanziMapper hanziMapper;
    private final PronunMapper pronunMapper;

    public Result audit(String versionId) throws IOException
    {
        List<ReferenceTsvService.Row> rows = referenceTsvService.readAllRows(versionId);
        Set<String> referenceCharacters = new HashSet<>();
        for (ReferenceTsvService.Row row : rows) referenceCharacters.add(row.values()[0]);

        List<HanziEntity> allEntities = hanziMapper.findForReferenceCheck(DIALECT.toString());
        Set<String> backendCharacters = new HashSet<>();
        for (HanziEntity entity : allEntities)
        {
            backendCharacters.add(entity.getSc());
            backendCharacters.add(entity.getTc());
        }
        List<MissingData> missingData = rows.stream()
                .filter(row -> !backendCharacters.contains(row.values()[0]))
                .map(row -> new MissingData(row.line(), row.values()[0]))
                .toList();
        Set<String> matchedSimplified = new HashSet<>();
        allEntities.stream()
                .filter(entity -> referenceCharacters.contains(entity.getSc()) || referenceCharacters.contains(entity.getTc()))
                .forEach(entity -> matchedSimplified.add(entity.getSc()));
        List<HanziEntity> matchedEntities = allEntities.stream()
                .filter(entity -> matchedSimplified.contains(entity.getSc()))
                .toList();
        Map<Integer, String> mandarinById = loadMandarin(matchedEntities);
        Map<String, ReadingInventory> backendReadings = backendReadings(matchedEntities, mandarinById);
        Map<String, List<BackendEntry>> backendEntries = backendEntries(matchedEntities, referenceCharacters);

        List<ReadingMismatch> readingMismatches = new ArrayList<>();
        Set<String> seenReadings = new HashSet<>();
        Map<String, Set<String>> referenceDialects = new HashMap<>();
        Map<String, Set<String>> referenceMandarin = new HashMap<>();
        for (ReferenceTsvService.Row row : rows)
        {
            String[] values = row.values();
            String character = values[0];
            ReadingInventory expected = backendReadings.get(character);
            if (expected == null) continue;
            try { referenceDialects.computeIfAbsent(character, ignored -> new HashSet<>())
                    .add(normalizeReferenceDialect(values[2])); } catch (RuntimeException ignored) { }
            try { referenceMandarin.computeIfAbsent(character, ignored -> new HashSet<>())
                    .add(normalizeMandarin(values[3])); } catch (RuntimeException ignored) { }
            try
            {
                Reading actual = new Reading(normalizeReferenceDialect(values[2]), normalizeMandarin(values[3]));
                boolean dialectMissing = !expected.dialects().contains(actual.dialect());
                boolean mandarinMissing = !expected.mandarin().contains(actual.mandarin());
                if ((dialectMissing || mandarinMissing) && seenReadings.add(character + "\0" + actual))
                    readingMismatches.add(new ReadingMismatch(row.line(), character, values[2], values[3],
                            displayReadings(expected), mismatchReason(values[2], values[3], dialectMissing, mandarinMissing)));
            }
            catch (RuntimeException exception)
            {
                if (seenReadings.add(character + "\0" + values[2] + "\0" + values[3]))
                    readingMismatches.add(new ReadingMismatch(row.line(), character, values[2], values[3],
                            displayReadings(expected), "TSV 中的拼音格式无法识别"));
            }
        }

        List<BackendSurplus> backendSurplus = new ArrayList<>();
        backendReadings.forEach((character, inventory) -> {
            if (!referenceCharacters.contains(character)) return;
            for (String dialect : inventory.dialects())
                if (!referenceDialects.getOrDefault(character, Set.of()).contains(dialect))
                    backendSurplus.add(new BackendSurplus(character, "方言拼音", displayStoredDialect(dialect)));
            for (String mandarin : inventory.explicitMandarin())
                if (!referenceMandarin.getOrDefault(character, Set.of()).contains(mandarin))
                    backendSurplus.add(new BackendSurplus(character, "汉语拼音", mandarin));
        });
        List<FormMismatch> formMismatches = formMismatches(matchedEntities);
        return new Result(matchedEntities.size(), missingData, readingMismatches, backendSurplus,
                formMismatches, backendEntries);
    }

    private Map<String, List<BackendEntry>> backendEntries(List<HanziEntity> entities, Set<String> characters)
    {
        Map<String, List<BackendEntry>> result = new LinkedHashMap<>();
        for (HanziEntity entity : entities)
        {
            BackendEntry entry = new BackendEntry(entity.getId(), entity.getSc(), entity.getTc());
            for (String character : new LinkedHashSet<>(List.of(entity.getSc(), entity.getTc())))
                if (characters.contains(character))
                    result.computeIfAbsent(character, ignored -> new ArrayList<>()).add(entry);
        }
        return result;
    }

    private Map<Integer, String> loadMandarin(List<HanziEntity> entities)
    {
        Set<Integer> ids = new LinkedHashSet<>();
        for (HanziEntity entity : entities)
            for (HanziPronunciation pronunciation : pronunciations(entity))
                ids.addAll(pronunciation.getMandarin());
        if (ids.isEmpty()) return Map.of();

        Map<Integer, String> result = new HashMap<>();
        for (MdrChar item : pronunMapper.getInfoByIds(ids))
            result.put(item.getMandarinId(), normalizeMandarin(MdrTool.getPinyinKey(item.getInfo())));
        return result;
    }

    private Map<String, ReadingInventory> backendReadings(List<HanziEntity> entities,
                                                           Map<Integer, String> mandarinById)
    {
        Map<String, ReadingInventory> result = new HashMap<>();
        for (HanziEntity entity : entities)
        {
            Set<String> dialects = new LinkedHashSet<>();
            Set<String> mandarin = new LinkedHashSet<>();
            Set<String> explicitMandarin = new LinkedHashSet<>();
            List<HanziPronunciation> pronunciations = pronunciations(entity);
            for (HanziPronunciation pronunciation : pronunciations)
            {
                dialects.add(normalizeStoredDialect(pronunciation.getPinyin().toString()));
                for (Integer id : pronunciation.getMandarin())
                {
                    String pinyin = mandarinById.get(id);
                    if (pinyin != null)
                    {
                        mandarin.add(pinyin);
                        explicitMandarin.add(pinyin);
                    }
                }
            }
            if (mandarin.isEmpty()) mandarin.addAll(automaticMandarin(entity));
            addInventory(result, entity.getSc(), dialects, mandarin, explicitMandarin);
            addInventory(result, entity.getTc(), dialects, mandarin, explicitMandarin);
        }
        return result;
    }

    private void addInventory(Map<String, ReadingInventory> result, String character,
                              Set<String> dialects, Set<String> mandarin, Set<String> explicitMandarin)
    {
        ReadingInventory inventory = result.computeIfAbsent(character,
                ignored -> new ReadingInventory(new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>()));
        inventory.dialects().addAll(dialects);
        inventory.mandarin().addAll(mandarin);
        inventory.explicitMandarin().addAll(explicitMandarin);
    }

    private Set<String> automaticMandarin(HanziEntity entity)
    {
        Set<String> result = new LinkedHashSet<>();
        for (String character : List.of(entity.getSc(), entity.getTc()))
            for (HanPinyin pinyin : HanPinyin.toPinyinList(character))
                result.add(normalizeMandarin(pinyin.getSyll() + pinyin.getTone()));
        return result;
    }

    private List<FormMismatch> formMismatches(List<HanziEntity> entities)
    {
        Map<String, Set<String>> actualBySimplified = new LinkedHashMap<>();
        for (HanziEntity entity : entities)
            actualBySimplified.computeIfAbsent(entity.getSc(), ignored -> new LinkedHashSet<>()).add(entity.getTc());

        List<FormMismatch> result = new ArrayList<>();
        actualBySimplified.forEach((simplified, actual) -> {
            Set<String> expected = new LinkedHashSet<>();
            ScTcCharacterConverter.toTraditional(simplified).standard()
                    .forEach(character -> expected.add(character.toString()));
            Set<String> missing = new LinkedHashSet<>(expected);
            missing.removeAll(actual);
            Set<String> unexpected = new LinkedHashSet<>(actual);
            unexpected.removeAll(expected);
            if (!missing.isEmpty() || !unexpected.isEmpty())
                result.add(new FormMismatch(simplified, List.copyOf(expected), List.copyOf(actual),
                        List.copyOf(missing), List.copyOf(unexpected)));
        });
        return result;
    }

    private List<HanziPronunciation> pronunciations(HanziEntity entity)
    {
        return JsonTool.readJson(entity.getPinyin(), new TypeReference<>() {});
    }

    private String normalizeReferenceDialect(String pinyin)
    {
        String standard = pinyin.trim().toLowerCase(Locale.ROOT).replace("eo", "ọ").replace("oe", "ọ");
        LACPinyin value = LACPinyin.tryOf(KeyboardPinyin.of(standard), LACPinyin.KeyboardScheme.STANDARD)
                .getValueDirectly("TSV 中的南昌话新方案拼音无效：" + pinyin);
        return value.toDatabasePinyin().toString(true);
    }

    private String normalizeStoredDialect(String pinyin)
    {
        LACPinyin value = DIALECT.trustedCreatePinyin(pinyin.trim());
        return value.toDatabasePinyin().toString(true);
    }

    private String normalizeMandarin(String pinyin)
    {
        HanPinyin value = HanPinyin.of(pinyin.trim());
        return value.getSyll() + value.getTone();
    }

    private List<String> displayReadings(ReadingInventory readings)
    {
        List<String> result = new ArrayList<>();
        String dialects = readings.dialects().stream()
                .map(this::displayStoredDialect)
                .collect(java.util.stream.Collectors.joining("、"));
        result.add("方言：" + (dialects.isEmpty() ? "无" : dialects));
        result.add("汉语：" + (readings.mandarin().isEmpty() ? "无" : String.join("、", readings.mandarin())));
        return result;
    }

    private String displayStoredDialect(String pinyin)
    {
        LACPinyin value = DIALECT.trustedCreatePinyin(pinyin);
        return value.toKeyboardPinyin().toString();
    }

    private String mismatchReason(String dialect, String mandarin, boolean dialectMissing, boolean mandarinMissing)
    {
        if (dialectMissing && mandarinMissing)
            return "后端缺方言拼音 " + dialect + "、汉语拼音 " + mandarin;
        return dialectMissing ? "后端缺方言拼音 " + dialect : "后端缺汉语拼音 " + mandarin;
    }

    private record Reading(String dialect, String mandarin) {}
    private record ReadingInventory(Set<String> dialects, Set<String> mandarin, Set<String> explicitMandarin) {}
    public record MissingData(int line, String character) {}
    public record ReadingMismatch(int line, String character, String dialectPinyin, String mandarinPinyin,
                                  List<String> backendReadings, String reason) {}
    public record FormMismatch(String simplified, List<String> expectedTraditional, List<String> actualTraditional,
                               List<String> missingTraditional, List<String> unexpectedTraditional) {}
    public record BackendSurplus(String character, String type, String pinyin) {}
    public record BackendEntry(Integer id, String simplified, String traditional) {}
    public record Result(int matchedBackendRows, List<MissingData> missingData,
                         List<ReadingMismatch> readingMismatches,
                         List<BackendSurplus> backendSurplus, List<FormMismatch> formMismatches,
                         Map<String, List<BackendEntry>> backendEntries) {}
}
