package com.shuowen.yuzong.study.curriculum.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.shuowen.yuzong.util.json.JsonTool.toJson;

/**
 * 部分的编辑对象，负责前后端 JSON 与数据库字段之间的转换和基础校验。
 */
@Data
@NoArgsConstructor
public class StudyPartUpdate
{
    private Integer id;
    private Integer partOrder = 1;
    private ScTcText title;
    private ScTcText description;

    public StudyPartUpdate(StudyPartEntity entity)
    {
        id = entity.getId();
        partOrder = entity.getPartOrder();
        title = readText(entity.getTitle());
        description = readText(entity.getDescription());
    }

    public StudyPartEntity toEntity(Dialect dialect)
    {
        ObjectTool.asserts(title != null, "部分标题不能为空");
        ObjectTool.asserts(!title.getSc().toString().isBlank(), "部分简体标题不能为空");
        ObjectTool.asserts(!title.getTc().toString().isBlank(), "部分繁体标题不能为空");
        ObjectTool.asserts(partOrder != null && partOrder > 0, "部分顺序必须大于 0");

        StudyPartEntity entity = new StudyPartEntity();
        entity.setId(id);
        entity.setDialect(dialect.toString());
        entity.setPartOrder(partOrder);
        entity.setTitle(toJson(title));
        entity.setDescription(toJson(description == null ? new ScTcText("", "") : description));
        return entity;
    }

    private static ScTcText readText(String json)
    {
        if (json == null || json.isBlank()) return new ScTcText("", "");
        return JsonTool.readJson(json, new TypeReference<>() {});
    }
}
