package com.shuowen.yuzong.dict.hanzi.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.dict.hanzi.model.HanziEntity;
import com.shuowen.yuzong.dict.hanzi.model.HanziSimilar;
import com.shuowen.yuzong.dict.data.domain.setting.NoteTag;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.ext.list.ListTool;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcChar;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.TextPinyinIPA;
import com.shuowen.yuzong.util.tuple.Pair;
import com.shuowen.yuzong.util.tuple.Range;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;

@Data
@NoArgsConstructor
public class HanziUpdate
{
    private Integer id;
    private ScTcChar hanzi;
    private List<HanziPronunciation> pinyin;
    private Integer special;
    private List<Similar> similar;
    private List<Pair<String, ScTcText>> note;
    private Integer status;

    @Data
    @NoArgsConstructor
    public static class Similar
    {
        private Integer id;
        private ScTcChar text;

        public Similar(HanziSimilar value)
        {
            id = value.getId();
            text = new ScTcChar(value.getSc(), value.getTc());
        }

        public HanziSimilar transfer(int hanziId)
        {
            var value = new HanziSimilar();
            value.setId(id);
            value.setHanziId(hanziId);
            value.setSc(text.getSc().toString());
            value.setTc(text.getTc().toString());
            return value;
        }
    }

    public HanziUpdate(Dialect dialect, HanziEntity entity, List<HanziSimilar> similar)
    {
        id = entity.getId();
        hanzi = new ScTcChar(entity.getSc(), entity.getTc());
        var storedPinyin = JsonTool.<List<HanziPronunciation>>readJson(entity.getPinyin(), new TypeReference<>() {});
        pinyin = ListTool.mapping(storedPinyin, item -> new HanziPronunciation(
                dialect.trustedCreatePinyin(item.getPinyin().toString()).toKeyboardPinyin(),
                item.getCode(), item.getTag(), item.getMandarin()));
        special = entity.getSpecial();
        this.similar = ListTool.mapping(similar, Similar::new);
        note = ListTool.mapping(HanziNoteTool.readForEdit(entity.getNote()),
                i -> Pair.of(i.getLeft(),
                        i.getRight().map(str -> TextPinyinIPA.transferPinyin(str, dialect, true))));
        status = entity.getStatus();
    }

    public Pair<HanziEntity, List<HanziSimilar>> checkAndTransfer(Dialect dialect)
    {
        ObjectTool.asserts(id != null, "缺少汉字编号");
        ObjectTool.asserts(pinyin != null && !pinyin.isEmpty(), "至少需要一个拼音");

        var normalized = ListTool.mapping(pinyin, i -> i.normalize(dialect));
        var unique = new HashSet<String>();
        for (var item : normalized)
            ObjectTool.asserts(unique.add(item.getPinyin().toString()), "同一个汉字不能包含重复拼音");

        var entity = new HanziEntity();
        entity.setId(id);
        entity.setSc(hanzi.getSc().toString());
        entity.setTc(hanzi.getTc().toString());
        entity.setPinyin(JsonTool.toJson(normalized, "[]"));
        ObjectTool.asserts(Range.close(0, 4).contains(special), "特殊性标记无效");
        entity.setSpecial(special);
        List<Pair<NoteTag, ScTcText>> normalizedNotes = HanziNoteTool.normalize(note);
        entity.setNote(JsonTool.toJson(ListTool.mapping(normalizedNotes, i -> Pair.of(
                i.getLeft(),
                i.getRight().map(str -> TextPinyinIPA.transferPinyin(str, dialect, false))
        )), "[]"));
        entity.setStatus(status);

        pinyin = normalized;
        return Pair.of(entity, ListTool.mapping(similar, i -> i.transfer(id)));
    }
}
