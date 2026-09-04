package com.shuowen.yuzong.study.loadingtext.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LoadingTextUpdate
{
    private Integer id;
    private ScTcText tip;
    private List<Dialect> tag = List.of();

    public LoadingTextUpdate(LoadingTextEntity entity)
    {
        id = entity.getId();
        tip = readTip(entity.getTip());
        tag = readTag(entity.getTag());
    }

    public LoadingTextEntity toEntity()
    {
        ObjectTool.asserts(tip != null, "提示语不能为空");

        LoadingTextEntity entity = new LoadingTextEntity();
        entity.setId(id);
        entity.setTip(JsonTool.toJson(tip));
        entity.setTag(JsonTool.toJson(tag == null ? List.of() : tag.stream().distinct().toList()));
        return entity;
    }

    public static ScTcText readTip(String json)
    {
        ScTcText result = JsonTool.readJson(json, new TypeReference<>() {});
        if (result == null) throw new IllegalArgumentException("提示语 JSON 无效"+json);
        return result;
    }

    public static List<Dialect> readTag(String json)
    {
        List<Dialect> result = JsonTool.readJson(json, new TypeReference<>() {});
        return result == null ? List.of() : result;
    }
}
