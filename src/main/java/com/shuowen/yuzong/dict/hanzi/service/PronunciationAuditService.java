package com.shuowen.yuzong.dict.hanzi.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.dict.ciyu.CiyuMapper;
import com.shuowen.yuzong.dict.data.model.Word.CiyuEntity;
import com.shuowen.yuzong.dict.hanzi.domain.HanziPronunciation;
import com.shuowen.yuzong.dict.hanzi.mapper.HanziMapper;
import com.shuowen.yuzong.linguistics.pinyinv2.UniPinyin;
import com.shuowen.yuzong.linguistics.util.SplitedPinyin;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.json.JsonTool;
import org.springframework.stereotype.Service;

import java.util.*;

/** 只读核查字表与词条的逐字读音；结果表示数据不一致，不判断哪一方正确。 */
@Service
public class PronunciationAuditService
{
    public record Finding(String type, Integer wordId, String word, int position, String character,
                          String traditionalCharacter,
                          String wordPinyin, List<String> hanziPinyins,
                          String wordDisplay, List<String> hanziDisplays, Integer hanziId) {}

    private record CharacterPair(String simplified, String traditional) {}

    private final HanziMapper hanziMapper;
    private final CiyuMapper ciyuMapper;

    public PronunciationAuditService(HanziMapper hanziMapper, CiyuMapper ciyuMapper)
    {
        this.hanziMapper = hanziMapper;
        this.ciyuMapper = ciyuMapper;
    }

    public List<Finding> check(Dialect dialect)
    {
        Map<CharacterPair, List<String>> table = new HashMap<>();
        Map<CharacterPair, Set<Integer>> hanziIds = new HashMap<>();
        Set<String> knownCharacters = new HashSet<>();
        for (var row : hanziMapper.getAllHanzi(dialect.toString()))
        {
            if (row.getSc() == null || row.getTc() == null) continue;
            CharacterPair pair = new CharacterPair(row.getSc(), row.getTc());
            hanziIds.computeIfAbsent(pair, ignored -> new LinkedHashSet<>()).add(row.getId());
            knownCharacters.add(row.getSc());
            knownCharacters.add(row.getTc());
            List<HanziPronunciation> pronunciations = JsonTool.readJson(row.getPinyin(), new TypeReference<>() {});
            if (pronunciations == null) continue;
            for (var pronunciation : pronunciations)
                if (pronunciation != null && pronunciation.getPinyin() != null)
                    table.computeIfAbsent(pair, ignored -> new ArrayList<>())
                            .add(pronunciation.getPinyin().toString());
        }

        List<Finding> result = new ArrayList<>();
        for (CiyuEntity word : ciyuMapper.getAllCiyu(dialect.toString()))
            checkWord(dialect, word, table, hanziIds, knownCharacters, result);
        return result;
    }

    private void checkWord(Dialect dialect, CiyuEntity word, Map<CharacterPair, List<String>> table,
                           Map<CharacterPair, Set<Integer>> hanziIds, Set<String> knownCharacters,
                           List<Finding> result)
    {
        List<SplitedPinyin> pinyins = JsonTool.readJson(word.getMainPy(), new TypeReference<>() {});
        if (pinyins == null) pinyins = List.of();
        String[] sc = codePoints(word.getSc());
        String[] tc = codePoints(word.getTc());
        if (sc.length != tc.length || sc.length != pinyins.size())
        {
            result.add(new Finding("UNALIGNED", word.getId(), word.getSc(), -1, null, null,
                    null, List.of(), null, List.of(), null));
            return;
        }
        for (int i = 0; i < pinyins.size(); i++)
        {
            String raw = pinyins.get(i) == null ? null : pinyins.get(i).toString();
            CharacterPair pair = new CharacterPair(sc[i], tc[i]);
            Set<Integer> ids = hanziIds.getOrDefault(pair, Set.of());
            Integer hanziId = ids.size() == 1 ? ids.iterator().next() : null;
            UniPinyin actual = parse(dialect, raw);
            if (actual == null)
            {
                result.add(new Finding("INVALID_WORD_PINYIN", word.getId(), word.getSc(), i, sc[i], tc[i],
                        raw, List.of(), null, List.of(), hanziId));
                continue;
            }
            List<String> candidates = table.getOrDefault(pair, List.of());
            if (candidates.isEmpty())
            {
                String type = ids.isEmpty()
                        ? knownCharacters.contains(sc[i]) && knownCharacters.contains(tc[i]) ? "FORM_MISMATCH" : "MISSING_HANZI"
                        : "MISSING_HANZI_PINYIN";
                result.add(new Finding(type, word.getId(), word.getSc(), i, sc[i], tc[i],
                        raw, List.of(), display(actual), List.of(), hanziId));
                continue;
            }
            boolean variant = false;
            boolean equal = false;
            boolean validCandidate = false;
            for (String candidate : candidates)
            {
                UniPinyin expected = parse(dialect, candidate);
                if (expected == null) continue;
                validCandidate = true;
                if (actual.equals(expected)) equal = true;
                else if (actual.matches(expected)) variant = true;
            }
            if (!equal)
                result.add(new Finding(!validCandidate ? "INVALID_HANZI_PINYIN" : variant ? "VARIANT" : "CONFLICT",
                        word.getId(), word.getSc(), i,
                        sc[i], tc[i], raw, List.copyOf(new LinkedHashSet<>(candidates)), display(actual),
                        candidates.stream().map(candidate -> display(parse(dialect, candidate)))
                                .filter(Objects::nonNull).distinct().toList(), hanziId));
        }
    }

    private UniPinyin parse(Dialect dialect, String value)
    {
        if (value == null) return null;
        try
        {
            var parsed = dialect.tryCreatePinyin(SplitedPinyin.of(value));
            return parsed.isEmpty() ? null : parsed.getValue();
        } catch (RuntimeException ignored)
        {
            return null;
        }
    }

    private String display(UniPinyin pinyin)
    {
        return pinyin == null ? null : pinyin.toRPinyin().toString();
    }

    private String[] codePoints(String text)
    {
        return text == null ? new String[0] : text.codePoints()
                .mapToObj(Character::toString).toArray(String[]::new);
    }
}
