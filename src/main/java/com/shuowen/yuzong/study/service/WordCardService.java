package com.shuowen.yuzong.study.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.study.data.dto.WordCardPractice;
import com.shuowen.yuzong.study.data.mapper.WordCardMapper;
import com.shuowen.yuzong.study.data.model.WordCardEntity;
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
        return mapper.findRandom(dialect.toString(), PRACTICE_SIZE).stream()
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
