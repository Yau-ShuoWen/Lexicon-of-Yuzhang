package com.shuowen.yuzong.study.curriculum.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.ext.other.ObjectTool;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.shuowen.yuzong.util.json.JsonTool.toJson;

/**
 * 关卡编辑对象。
 *
 * <p>第一版只开放配对题所需的配置，后续新增题型时可以继续扩展这个对象，
 * 不需要改变部分的结构。</p>
 */
@Data
@NoArgsConstructor
public class StudyLevelUpdate
{
    private Integer id;
    private Integer partId;
    private Integer levelOrder = 1;
    private ScTcText title;
    private ScTcText description;
    private String questionType = "match_pair";
    private Integer questionCount = 3;
    private Integer pairCount = 5;
    private List<Integer> cardIds = new ArrayList<>();
    private Integer status = 0;

    public StudyLevelUpdate(StudyLevelEntity entity)
    {
        id = entity.getId();
        partId = entity.getPartId();
        levelOrder = entity.getLevelOrder();
        title = readText(entity.getTitle());
        description = readText(entity.getDescription());
        questionType = entity.getQuestionType();
        questionCount = entity.getQuestionCount();
        pairCount = entity.getPairCount();
        cardIds = readCardIds(entity.getCardIds());
        status = entity.getStatus();
    }

    public StudyLevelEntity toEntity()
    {
        ObjectTool.asserts(partId != null && partId > 0, "关卡所属部分不能为空");
        ObjectTool.asserts(levelOrder != null && levelOrder > 0, "关卡顺序必须大于 0");
        ObjectTool.asserts(title != null, "关卡标题不能为空");
        ObjectTool.asserts("match_pair".equals(questionType), "当前只支持 match_pair 题型");
        ObjectTool.asserts(questionCount != null && questionCount > 0 && questionCount <= 50,
                "题目数量必须在 1 到 50 之间");
        ObjectTool.asserts(pairCount != null && pairCount >= 2 && pairCount <= 20,
                "每题词卡数量必须在 2 到 20 之间");
        ObjectTool.asserts(status != null && (status == 0 || status == 1), "关卡状态无效");

        List<Integer> ids = cardIds == null ? List.of() : cardIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();
        ObjectTool.asserts(ids.isEmpty() || ids.size() == pairCount,
                "指定词卡数量必须等于每题词卡数量");

        StudyLevelEntity entity = new StudyLevelEntity();
        entity.setId(id);
        entity.setPartId(partId);
        entity.setLevelOrder(levelOrder);
        entity.setTitle(toJson(title));
        entity.setDescription(toJson(description == null ? new ScTcText("", "") : description));
        entity.setQuestionType(questionType);
        entity.setQuestionCount(questionCount);
        entity.setPairCount(pairCount);
        entity.setCardIds(toJson(ids));
        entity.setStatus(status);
        return entity;
    }

    private static ScTcText readText(String json)
    {
        if (json == null || json.isBlank()) return new ScTcText("", "");
        return JsonTool.readJson(json, new TypeReference<>() {});
    }

    private static List<Integer> readCardIds(String json)
    {
        if (json == null || json.isBlank()) return new ArrayList<>();
        List<Integer> result = JsonTool.readJson(json, new TypeReference<>() {});
        return result == null ? new ArrayList<>() : new ArrayList<>(result);
    }
}
