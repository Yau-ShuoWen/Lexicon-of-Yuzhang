package com.shuowen.yuzong.dict.hanzi.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.dict.hanzi.model.HanziEntity;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcChar;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UChar;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Twin;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class HanziItem
{
    private final Integer id;
    private final UChar hanzi;
    private final ScTcChar hanzis;
    private final List<HanziPronunciation> pinyin;
    private final Integer special;
    private final List<UChar> similar;
    private final List<Twin<UString>> note;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    protected HanziItem(HanziEntity entity, Language language)
    {
        id = entity.getId();
        hanzis = new ScTcChar(entity.getSc(), entity.getTc());
        hanzi = hanzis.get(language);
        pinyin = JsonTool.readJson(entity.getPinyin(), new TypeReference<>() {});
        special = entity.getSpecial();
        similar = ListTool.mapping(
                JsonTool.readJson(entity.getSimilar(), new TypeReference<List<ScTcChar>>() {}),
                i -> i.get(language));
        note = ListTool.mapping(
                JsonTool.readJson(entity.getNote(), new TypeReference<List<Map<String, ScTcText>>>() {}),
                i -> Twin.of(i.get("tag").get(language), i.get("content").get(language)));
        createdAt = entity.getCreatedAt();
        updatedAt = entity.getUpdatedAt();
    }
}
