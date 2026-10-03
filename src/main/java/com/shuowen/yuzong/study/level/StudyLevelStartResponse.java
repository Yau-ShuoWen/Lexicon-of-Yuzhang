package com.shuowen.yuzong.study.level;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StudyLevelStartResponse
{
    private String attemptId;
    private String questionType;
    private List<StudyLevelQuestion> questions;
}
