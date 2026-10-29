package com.shuowen.yuzong.reference.tsv;

import com.shuowen.yuzong.dict.data.mapper.LogMapper;
import com.shuowen.yuzong.dict.hanzi.domain.HanziPronunciation;
import com.shuowen.yuzong.dict.hanzi.mapper.HanziMapper;
import com.shuowen.yuzong.dict.hanzi.model.HanziEntity;
import com.shuowen.yuzong.linguistics.pinyinv2.lac.LACPinyin;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyin;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcCharacterConverter;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReferenceTsvImportService
{
    private final ReferenceTsvClassificationService classificationService;
    private final HanziMapper hanziMapper;
    private final LogMapper logMapper;

    @Transactional(rollbackFor = Exception.class)
    public int importCharacters(String versionId, List<String> characters) throws IOException
    {
        if (characters == null || characters.isEmpty() || characters.size() > 1000)
            throw new IllegalArgumentException("请选择 1 至 1000 个汉字");
        Set<String> selected = new LinkedHashSet<>(characters);
        if (selected.size() != characters.size()) throw new IllegalArgumentException("所选汉字有重复");

        Map<String, List<ReferenceTsvClassificationService.ClassifiedRow>> rowsByCharacter = new LinkedHashMap<>();
        for (var row : classificationService.classify(versionId).rows())
            if (selected.contains(row.values()[0]))
                rowsByCharacter.computeIfAbsent(row.values()[0], ignored -> new ArrayList<>()).add(row);

        List<HanziEntity> pending = new ArrayList<>();
        Set<String> claimedForms = new LinkedHashSet<>();
        for (String character : selected)
        {
            List<ReferenceTsvClassificationService.ClassifiedRow> rows = rowsByCharacter.get(character);
            if (rows == null || rows.isEmpty()) throw new IllegalArgumentException("原文件中没有汉字：" + character);
            if (rows.stream().anyMatch(row -> !row.general()))
                throw new IllegalArgumentException("不是一般汉字，不能直接导入：" + character);
            if (character.codePointCount(0, character.length()) != 1)
                throw new IllegalArgumentException("不是单字：" + character);

            var traditional = ScTcCharacterConverter.toTraditional(character).standard();
            if (traditional.size() != 1)
                throw new IllegalStateException("一般汉字的繁体转换结果不唯一，请检查资料解析：" + character);
            String tc = traditional.get(0).toString();
            if (claimedForms.contains(character) || claimedForms.contains(tc))
                throw new IllegalArgumentException("所选汉字存在重复字形：" + character);
            claimedForms.add(character);
            claimedForms.add(tc);
            if (!hanziMapper.findHanziByScTc(character, "lac").isEmpty()
                    || !hanziMapper.findHanziByScTc(tc, "lac").isEmpty())
                throw new IllegalArgumentException("数据库已有该字：" + character);

            List<HanziPronunciation> pronunciations = new ArrayList<>();
            Set<String> seen = new LinkedHashSet<>();
            for (var row : rows)
            {
                String raw = row.values()[2];
                String standard = raw.trim().toLowerCase(Locale.ROOT).replace("eo", "ọ").replace("oe", "ọ");
                LACPinyin parsed = LACPinyin.tryOf(KeyboardPinyin.of(standard), LACPinyin.KeyboardScheme.STANDARD)
                        .getValueDirectly("方言拼音无法解析：" + character + " " + raw);
                String stored = parsed.toDatabasePinyin().toString(true);
                if (seen.add(stored)) pronunciations.add(new HanziPronunciation(
                        KeyboardPinyin.of(stored), parsed.getWeight(), new ScTcText("文读", "文讀"), List.of()));
            }
            HanziEntity entity = new HanziEntity();
            entity.setSc(character);
            entity.setTc(tc);
            entity.setPinyin(JsonTool.toJson(pronunciations, "[]"));
            entity.setSpecial(0);
            entity.setNote("[]");
            entity.setStatus(1);
            entity.setSource(2);
            pending.add(entity);
        }

        for (HanziEntity entity : pending)
        {
            hanziMapper.insertImportedLacChar(entity);
            logMapper.insertChar("lac", null, JsonTool.toJson(entity), "C");
        }
        return pending.size();
    }
}
