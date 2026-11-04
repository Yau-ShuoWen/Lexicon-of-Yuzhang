package com.shuowen.yuzong.study.level;

import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 当前阶段的临时关卡尝试。
 *
 * <p>题目系统尚未落库前，尝试只保存在后端内存中。后续接入章节、单元和进度系统时，
 * 这个领域对象可以替换为数据库实体，而控制器接口保持不变。</p>
 */
@Getter
public class StudyLevelAttempt
{
    private final String attemptId;
    private final Integer userId;
    private final Language language;
    private final Dialect dialect;
    private final List<StudyLevelQuestion> questions;
    private final LocalDateTime createdAt;
    private boolean completed;

    public StudyLevelAttempt(String attemptId,
                             Integer userId,
                             Language language,
                             Dialect dialect,
                             List<StudyLevelQuestion> questions,
                             LocalDateTime createdAt)
    {
        this.attemptId = attemptId;
        this.userId = userId;
        this.language = language;
        this.dialect = dialect;
        this.questions = List.copyOf(questions);
        this.createdAt = createdAt;
    }

    public void markCompleted()
    {
        completed = true;
    }
}
