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
                          String wordPinyin, List<String> hanziPinyins,
                          String wordDisplay, List<String> hanziDisplays) {}

    private final HanziMapper hanziMapper;
    private final CiyuMapper ciyuMapper;

    public PronunciationAuditService(HanziMapper hanziMapper, CiyuMapper ciyuMapper)
    {
        this.hanziMapper = hanziMapper;
        this.ciyuMapper = ciyuMapper;
    }

    public List<Finding> check(Dialect dialect)
    {
        Map<String, List<String>> table = new HashMap<>();
        for (var row : hanziMapper.getAllHanzi(dialect.toString()))
        {
            List<HanziPronunciation> pronunciations = JsonTool.readJson(row.getPinyin(), new TypeReference<>() {});
            if (pronunciations == null) continue;
            for (String character : new String[]{row.getSc(), row.getTc()})
                if (character != null && !character.isEmpty())
                    for (var pronunciation : pronunciations)
                        if (pronunciation != null && pronunciation.getPinyin() != null)
                            table.computeIfAbsent(character, ignored -> new ArrayList<>())
                                    .add(pronunciation.getPinyin().toString());
        }

        List<Finding> result = new ArrayList<>();
        for (CiyuEntity word : ciyuMapper.getAllCiyu(dialect.toString()))
            checkWord(dialect, word, table, result);
        return result;
    }

    private void checkWord(Dialect dialect, CiyuEntity word, Map<String, List<String>> table, List<Finding> result)
    {
        List<SplitedPinyin> pinyins = JsonTool.readJson(word.getMainPy(), new TypeReference<>() {});
        if (pinyins == null) pinyins = List.of();
        String[] sc = codePoints(word.getSc());
        String[] tc = codePoints(word.getTc());
        if (sc.length != tc.length || sc.length != pinyins.size())
        {
            result.add(new Finding("UNALIGNED", word.getId(), word.getSc(), -1, null, null, List.of(), null, List.of()));
            return;
        }
        for (int i = 0; i < pinyins.size(); i++)
        {
            String raw = pinyins.get(i) == null ? null : pinyins.get(i).toString();
            UniPinyin actual = parse(dialect, raw);
            if (actual == null)
            {
                result.add(new Finding("INVALID_WORD_PINYIN", word.getId(), word.getSc(), i, sc[i], raw, List.of(), null, List.of()));
                continue;
            }
            Set<String> characters = new LinkedHashSet<>(Arrays.asList(sc[i], tc[i]));
            List<String> candidates = new ArrayList<>();
            for (String character : characters) candidates.addAll(table.getOrDefault(character, List.of()));
            if (candidates.isEmpty())
            {
                result.add(new Finding("MISSING_HANZI", word.getId(), word.getSc(), i, sc[i], raw, List.of(), display(actual), List.of()));
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
                        sc[i], raw, List.copyOf(new LinkedHashSet<>(candidates)), display(actual),
                        candidates.stream().map(candidate -> display(parse(dialect, candidate)))
                                .filter(Objects::nonNull).distinct().toList()));
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
