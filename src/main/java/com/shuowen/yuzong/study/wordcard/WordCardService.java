package com.shuowen.yuzong.study.wordcard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordCardService
{
    private static final int PRACTICE_SIZE = 5;

    @Autowired
    private WordCardMapper mapper;

    public List<WordCardPractice> getRandomCards(Dialect dialect, Language language)
    {
        return getRandomCards(dialect, language, PRACTICE_SIZE);
    }

    public List<WordCardPractice> getRandomCards(Dialect dialect, Language language, int limit)
    {
        if (limit <= 0) throw new IllegalArgumentException("词卡数量必须大于 0");
        return mapper.findRandom(dialect.toString(), limit).stream()
                .map(card -> toPractice(card, dialect, language))
                .toList();
    }

    public List<WordCardPractice> getCardsByIds(Dialect dialect, Language language, List<Integer> ids)
    {
        if (ids == null || ids.isEmpty()) return List.of();
        return mapper.findByIds(ids.stream().filter(id -> id != null && id > 0).distinct().toList(), dialect.toString())
                .stream()
                .map(card -> toPractice(card, dialect, language))
                .toList();
    }

    private WordCardPractice toPractice(WordCardEntity card, Dialect dialect, Language language)
    {
        ScTcText putonghua = JsonTool.readJson(card.putonghua, new TypeReference<>() {});
        ScTcText wordText = JsonTool.readJson(card.word, new TypeReference<>() {});
        String mandarin = putonghua.get(language).toString();
        String word = wordText.get(language).toString();
        String pinyin = List.of(card.pinyin.trim().split("\\s+"))
                .stream()
                .map(py -> dialect.trustedCreatePinyin(py).toRPinyin().toString().trim())
                .reduce((left, right) -> left + " " + right)
                .orElse("");
        return new WordCardPractice(card.id, mandarin, word, pinyin);
    }
}
