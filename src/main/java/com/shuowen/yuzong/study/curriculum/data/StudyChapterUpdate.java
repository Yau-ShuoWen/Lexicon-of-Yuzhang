package com.shuowen.yuzong.study.curriculum.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.shuowen.yuzong.util.json.JsonTool.toJson;

/** 章节编辑对象。 */
@Data
@NoArgsConstructor
public class StudyChapterUpdate
{
    private Integer id;
    private Integer chapterOrder = 1;
    private ScTcText title;
    private ScTcText description;

    public StudyChapterEntity toEntity(Integer partId)
    {
        ObjectTool.asserts(partId != null && partId > 0, "章节所属部分不能为空");
        ObjectTool.asserts(chapterOrder != null && chapterOrder > 0, "章节顺序必须大于 0");
        ObjectTool.asserts(title != null, "章节标题不能为空");
        ObjectTool.asserts(!title.getSc().toString().isBlank(), "章节简体标题不能为空");
        ObjectTool.asserts(!title.getTc().toString().isBlank(), "章节繁体标题不能为空");

        StudyChapterEntity entity = new StudyChapterEntity();
        entity.setId(id);
        entity.setPartId(partId);
        entity.setChapterOrder(chapterOrder);
        entity.setTitle(toJson(title));
        entity.setDescription(toJson(description == null ? new ScTcText("", "") : description));
        return entity;
    }

    public StudyChapterUpdate(StudyChapterEntity entity)
    {
        id = entity.getId();
        chapterOrder = entity.getChapterOrder();
        title = readText(entity.getTitle());
        description = readText(entity.getDescription());
    }

    private static ScTcText readText(String json)
    {
        if (json == null || json.isBlank()) return new ScTcText("", "");
        return JsonTool.readJson(json, new TypeReference<>() {});
    }
}
