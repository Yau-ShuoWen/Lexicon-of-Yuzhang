package com.shuowen.yuzong.study.curriculum.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;

/**
 * 学习部分的展示对象。
 *
 * <p>内容状态由下一级“章”计算，不作为可编辑字段。章模块尚未接入时，
 * 部分统一视为空，同时暂时禁止删除。</p>
 */
@Data
public class StudyPartCatalog
{
    private Integer id;
    private Integer partOrder;
    private ScTcText title;
    private ScTcText description;
    private ContentState contentState;
    private boolean deletable;

    public StudyPartCatalog(StudyPartEntity entity, boolean hasContent)
    {
        id = entity.getId();
        partOrder = entity.getPartOrder();
        title = JsonTool.readJson(entity.getTitle(), new TypeReference<>() {});
        description = JsonTool.readJson(entity.getDescription(), new TypeReference<>() {});
        contentState = hasContent ? ContentState.HAS_CONTENT : ContentState.EMPTY;
        deletable = !hasContent;
    }

    public enum ContentState
    {
        EMPTY,
        HAS_CONTENT
    }
}
