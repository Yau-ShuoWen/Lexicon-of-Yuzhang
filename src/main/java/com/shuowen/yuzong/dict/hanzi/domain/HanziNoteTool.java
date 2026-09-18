package com.shuowen.yuzong.dict.hanzi.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.dict.data.domain.setting.NoteTag;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Pair;
import com.shuowen.yuzong.util.tuple.Twin;

import java.util.ArrayList;
import java.util.List;

/** 汉字注释的新旧 JSON 格式适配；旧格式只读，新保存统一使用 NoteTag 代码。 */
final class HanziNoteTool
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private HanziNoteTool()
    {
    }

    static List<Pair<String, ScTcText>> readForEdit(String json)
    {
        List<Pair<String, ScTcText>> result = new ArrayList<>();
        if (json == null || json.isBlank()) return result;
        try
        {
            JsonNode root = MAPPER.readTree(json);
            if (!root.isArray()) return result;
            for (JsonNode item : root)
            {
                JsonNode tag = first(item, "left", "tag");
                JsonNode content = first(item, "right", "content");
                ScTcText text = readText(content);
                if (text != null) result.add(Pair.of(readTag(tag), text));
            }
            return result;
        }
        catch (Exception ignored)
        {
            return result;
        }
    }

    static List<Twin<UString>> readForShow(String json, Language language)
    {
        List<Twin<UString>> result = new ArrayList<>();
        for (var item : readForEdit(json))
        {
            UString label = NoteTag.find(item.getLeft())
                    .map(tag -> tag.getName().get(language))
                    .orElseGet(() -> UString.of(item.getLeft()));
            result.add(Twin.of(label, item.getRight().get(language)));
        }
        return result;
    }

    static List<Pair<NoteTag, ScTcText>> normalize(List<Pair<String, ScTcText>> notes)
    {
        List<Pair<NoteTag, ScTcText>> result = new ArrayList<>();
        if (notes == null) return result;
        for (var item : notes)
        {
            if (item == null || item.getRight() == null)
                throw new IllegalArgumentException("汉字注释内容不能为空");
            NoteTag tag = NoteTag.find(item.getLeft()).orElseThrow(
                    () -> new IllegalArgumentException("请先处理未知的汉字注释标签：" + item.getLeft()));
            result.add(Pair.of(tag, item.getRight()));
        }
        return result;
    }

    private static JsonNode first(JsonNode item, String preferred, String legacy)
    {
        JsonNode value = item.get(preferred);
        return value == null || value.isNull() ? item.get(legacy) : value;
    }

    private static String readTag(JsonNode node)
    {
        if (node == null || node.isNull()) return "";
        if (node.isTextual())
        {
            String value = node.asText().trim();
            return NoteTag.find(value).map(NoteTag::toString).orElse(value);
        }
        String sc = text(node.get("sc"));
        String tc = text(node.get("tc"));
        return NoteTag.find(tc).or(() -> NoteTag.find(sc)).map(NoteTag::toString)
                .orElseGet(() -> !tc.isBlank() ? tc : sc);
    }

    private static ScTcText readText(JsonNode node)
    {
        if (node == null || !node.isObject()) return null;
        String sc = text(node.get("sc"));
        String tc = text(node.get("tc"));
        return new ScTcText(sc, tc);
    }

    private static String text(JsonNode node)
    {
        return node == null || node.isNull() ? "" : node.asText();
    }
}
