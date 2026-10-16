package com.shuowen.yuzong.dict.pinyin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.TextPinyinIPA;
import com.shuowen.yuzong.util.text.ScTcText;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PinyinService
{
    @Autowired
    private PinyinMapper m;

    public List<String> getKey(Dialect d)
    {
        return m.getEditKey(d.toString());
    }

    public ScTcText getNote(Dialect d, String key)
    {
        ScTcText stored = JsonTool.readJson(m.getNote(d.toString(), key), new TypeReference<>() {});
        return stored == null ? null : stored.map(text -> TextPinyinIPA.transferPinyin(text, d, true));
    }

    public void updateNote(Dialect d, String key, ScTcText note)
    {
        ScTcText stored = note.map(text -> TextPinyinIPA.transferPinyin(text, d, false));
        m.updateNote(d.toString(), key, JsonTool.toJson(stored));
    }

    private static PinyinService instance;

    @PostConstruct
    public void init()
    {
        instance = this;
    }

    public static List<PinyinItem> getTableItem(Dialect d, String key)
    {
        return instance.m.getTableItem(d.toString(), key);
    }
}
