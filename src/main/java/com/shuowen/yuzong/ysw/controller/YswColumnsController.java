package com.shuowen.yuzong.ysw.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.util.map.KV;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class YswColumnsController
{
    private static final String KEY = "ysw-columns:v1";
    private final ObjectMapper json;

    public YswColumnsController(ObjectMapper json)
    {
        this.json = json;
    }

    @GetMapping("/personal/columns")
    public APIResponse<JsonNode> read()
    {
        String stored = KV.get(KEY);
        if (stored == null || stored.isBlank()) return APIResponse.success(null);
        try { return APIResponse.success(json.readTree(stored)); }
        catch (Exception e) { throw new IllegalStateException("譜絡格欄目数据无法解析", e); }
    }

    @PostMapping("/edit/ysw-columns")
    @Transactional
    public APIResponse<Void> write(@RequestBody JsonNode document)
    {
        validate(document);
        String value = document.toString();
        if (value.length() > 100_000) throw new IllegalArgumentException("栏目数据过大");
        KV.set(KEY, value);
        return APIResponse.success();
    }

    private static void validate(JsonNode document)
    {
        if (document == null || document.path("version").asInt() != 1 || !document.path("groups").isArray())
            throw new IllegalArgumentException("栏目文档格式不正确");
        pair(document.path("title"));
        pair(document.path("quote"));
        Set<String> ids = new HashSet<>();
        for (JsonNode group : document.path("groups"))
        {
            id(group, ids);
            pair(group.path("title"));
            if (!group.path("projects").isArray()) throw new IllegalArgumentException("项目列表格式不正确");
            for (JsonNode project : group.path("projects"))
            {
                id(project, ids);
                pair(project.path("name"));
                pair(project.path("note"));
                String repo = project.path("repo").asText("");
                String url = project.path("url").asText("");
                if (repo.isBlank() || !url.matches("https://github\\.com/[A-Za-z0-9-]+/[A-Za-z0-9._-]+/?"))
                    throw new IllegalArgumentException("仓库名称或链接不正确");
            }
        }
    }

    private static void id(JsonNode node, Set<String> ids)
    {
        String id = node.path("id").asText("");
        if (id.isBlank() || !ids.add(id)) throw new IllegalArgumentException("栏目 ID 为空或重复");
    }

    private static void pair(JsonNode node)
    {
        if (!node.isObject() || !node.path("sc").isTextual() || !node.path("tc").isTextual()
                || node.path("sc").asText().isBlank() || node.path("tc").asText().isBlank())
            throw new IllegalArgumentException("请分别填写简体和繁体文案");
    }
}
