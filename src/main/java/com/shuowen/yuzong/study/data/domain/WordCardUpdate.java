package com.shuowen.yuzong.study.data.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.linguistics.util.KeyboardPinyinList;
import com.shuowen.yuzong.study.data.model.WordCardEntity;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class WordCardUpdate
{
    private Integer id;
    private ScTcText putonghua;
    private ScTcText word;
    private KeyboardPinyinList pinyin;

    public WordCardUpdate(WordCardEntity entity, Dialect dialect)
    {
        id = entity.id;
        putonghua = JsonTool.readJson(entity.putonghua, new TypeReference<>() {});
        word = JsonTool.readJson(entity.word, new TypeReference<>() {});

        List<String> databasePinyin = List.of(entity.pinyin.trim().split("\\s+"));
        pinyin = KeyboardPinyinList.of(ListTool.mapping(
                databasePinyin,
                item -> dialect.trustedCreatePinyin(item).toKeyboardPinyin()
        ));
    }

    public WordCardEntity checkAndTransfer(Dialect dialect)
    {
        ObjectTool.asserts(putonghua != null && word != null && pinyin != null, "词卡内容不完整");
        ObjectTool.asserts(word.length() == pinyin.size(), "方言词语长度和拼音数量不一致");

        WordCardEntity entity = new WordCardEntity();
        entity.id = id;
        entity.dialect = dialect.toString();
        entity.putonghua = JsonTool.toJson(putonghua);
        entity.word = JsonTool.toJson(word);
        entity.pinyin = String.join(" ", ListTool.mapping(
                pinyin.getPinyin(),
                item -> dialect.checkAndCreatePinyin(item).toDatabasePinyin().toString(true)
        ));
        return entity;
    }
}
