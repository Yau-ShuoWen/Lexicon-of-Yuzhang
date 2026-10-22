package com.shuowen.yuzong.dict.pinyin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.dict.data.mapper.KVSMapper;
import com.shuowen.yuzong.util.core.Dialect;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class PinyinDocumentService
{
    private static final ObjectMapper JSON = new ObjectMapper();
    private final KVSMapper kvs;
    private final PinyinMapper pinyin;
    private final PinyinDocumentMapper documents;

    public PinyinDocumentService(KVSMapper kvs, PinyinMapper pinyin, PinyinDocumentMapper documents)
    {
        this.kvs = kvs;
        this.pinyin = pinyin;
        this.documents = documents;
    }

    public JsonNode get(Dialect dialect)
    {
        String stored = documents.get(dialect.toString());
        if (stored == null || stored.isBlank()) return null;
        try { return JSON.readTree(stored); }
        catch (Exception e) { throw new IllegalStateException("拼音表数据无法解析", e); }
    }

    public Map<String, Object> legacy(Dialect dialect)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("layout", kvs.get("pinyin-table-display-json:" + dialect));
        result.put("entries", pinyin.getLegacyEntries(dialect.toString()));
        return result;
    }

    public void save(Dialect dialect, JsonNode document)
    {
        validate(document);
        String value = document.toString();
        if (value.length() > 1_000_000) throw new IllegalArgumentException("拼音表数据过大");
        documents.upsert(dialect.toString(), value);
    }

    private void validate(JsonNode document)
    {
        if (document == null || document.path("version").asInt() != 2 || !document.path("sections").isArray())
            throw new IllegalArgumentException("拼音表格式不正确");
        if (!document.path("unplaced").isArray())
            throw new IllegalArgumentException("未归类子项格式不正确");
        Set<String> itemKeys = new HashSet<>();
        Set<String> childKeys = new HashSet<>();
        Set<String> audioKeys = new HashSet<>();
        Set<String> ids = new HashSet<>();
        for (JsonNode section : document.path("sections"))
        {
            requireId(section, ids);
            String sectionCode = section.path("code").asText("").trim();
            if (sectionCode.isBlank() || !section.path("name").isObject())
                throw new IllegalArgumentException("分区名称或代码为空：" + sectionCode);
            if (!section.path("rows").isArray()) throw new IllegalArgumentException("分区缺少行");
            for (JsonNode row : section.path("rows"))
            {
                requireId(row, ids);
                if (!row.path("groups").isArray()) throw new IllegalArgumentException("拼音行格式不正确");
                for (JsonNode group : row.path("groups"))
                {
                    requireId(group, ids);
                    if (!group.path("items").isArray()) throw new IllegalArgumentException("拼音组格式不正确");
                    for (JsonNode item : group.path("items"))
                    {
                        requireId(item, ids);
                        String key = item.path("key").asText("").trim();
                        if (key.isEmpty() || !itemKeys.add(key)) throw new IllegalArgumentException("拼音格子的 key 为空或重复：" + key);
                        if (item.path("standard").asText("").isBlank())
                            throw new IllegalArgumentException("拼音格子的显示内容不能为空：" + key);
                        if (!item.path("children").isArray()) throw new IllegalArgumentException("子项格式不正确");
                        for (JsonNode child : item.path("children"))
                        {
                            requireId(child, ids);
                            String childKey = child.path("key").asText("").trim();
                            if (childKey.isEmpty() || !childKeys.add(childKey)) throw new IllegalArgumentException("子项 key 为空或重复：" + childKey);
                            String audioKey = child.path("audioKey").asText("").trim();
                            if (audioKey.isEmpty() || !audioKeys.add(audioKey)) throw new IllegalArgumentException("录音关联为空或重复：" + childKey);
                            if (child.path("title").asText("").isBlank()) throw new IllegalArgumentException("请填写子项标题：" + childKey);
                            if (!child.path("notes").isArray()) throw new IllegalArgumentException("解释项格式不正确");
                            for (JsonNode note : child.path("notes"))
                            {
                                requireId(note, ids);
                                if (!note.path("label").isObject() || !note.path("value").isObject())
                                    throw new IllegalArgumentException("解释项缺少名称或内容");
                            }
                        }
                    }
                }
            }
        }
        for (JsonNode child : document.path("unplaced"))
        {
            requireId(child, ids);
            String key = child.path("key").asText("").trim();
            if (key.isEmpty() || !childKeys.add(key)) throw new IllegalArgumentException("未归类子项 key 为空或重复：" + key);
            String audioKey = child.path("audioKey").asText("").trim();
            if (audioKey.isEmpty() || !audioKeys.add(audioKey)) throw new IllegalArgumentException("未归类子项录音关联为空或重复：" + key);
            if (!child.path("notes").isArray()) throw new IllegalArgumentException("未归类子项解释格式不正确：" + key);
            for (JsonNode note : child.path("notes"))
            {
                requireId(note, ids);
                if (!note.path("label").isObject() || !note.path("value").isObject())
                    throw new IllegalArgumentException("未归类子项解释缺少名称或内容：" + key);
            }
        }
    }

    private void requireId(JsonNode node, Set<String> ids)
    {
        String id = node.path("id").asText("");
        if (id.isBlank() || !ids.add(id)) throw new IllegalArgumentException("拼音表内部 ID 为空或重复");
    }
}
