package com.shuowen.yuzong.study.curriculum.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;

/**
 * 面向学习首页的关卡目录，不暴露编辑用的词卡配置。
 */
@Data
public class StudyLevelCatalog
{
    private Integer id;
    private Integer levelOrder;
    private ScTcText title;
    private ScTcText description;
    private String questionType;
    private Integer questionCount;
    private Integer status;

    public StudyLevelCatalog(StudyLevelEntity entity)
    {
        id = entity.getId();
        levelOrder = entity.getLevelOrder();
        title = JsonTool.readJson(entity.getTitle(), new TypeReference<>() {});
        description = JsonTool.readJson(entity.getDescription(), new TypeReference<>() {});
        questionType = entity.getQuestionType();
        questionCount = entity.getQuestionCount();
        status = entity.getStatus();
    }
}
