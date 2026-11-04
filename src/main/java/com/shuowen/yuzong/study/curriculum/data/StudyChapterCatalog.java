package com.shuowen.yuzong.study.curriculum.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import lombok.Data;

/** 章节列表的展示对象。 */
@Data
public class StudyChapterCatalog
{
    private Integer id;
    private Integer chapterOrder;
    private ScTcText title;
    private ScTcText description;
    private StudyPartCatalog.ContentState contentState;
    private boolean deletable;

    public StudyChapterCatalog(StudyChapterEntity entity)
    {
        id = entity.getId();
        chapterOrder = entity.getChapterOrder();
        title = JsonTool.readJson(entity.getTitle(), new TypeReference<>() {});
        description = JsonTool.readJson(entity.getDescription(), new TypeReference<>() {});
        contentState = StudyPartCatalog.ContentState.EMPTY;
        deletable = true;
    }
}
