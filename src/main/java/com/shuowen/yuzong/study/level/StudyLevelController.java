package com.shuowen.yuzong.study.level;

import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study/level")
public class StudyLevelController
{
    private final StudyLevelService service;

    public StudyLevelController(StudyLevelService service)
    {
        this.service = service;
    }

    @PostMapping("/start/{language}/{dialect}")
    public APIResponse<StudyLevelStartResponse> start(
            @RequestParam String t,
            @PathVariable Language language,
            @PathVariable Dialect dialect)
    {
        try
        {
            return APIResponse.success(service.start(t, language, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/start/{language}/{dialect}/{levelId}")
    public APIResponse<StudyLevelStartResponse> startConfigured(
            @RequestParam String t,
            @PathVariable Language language,
            @PathVariable Dialect dialect,
            @PathVariable Integer levelId)
    {
        try
        {
            return APIResponse.success(service.start(t, language, dialect, levelId));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/finish")
    public APIResponse<StudyLevelFinishResponse> finish(
            @RequestParam String t,
            @RequestBody StudyLevelFinishRequest request)
    {
        try
        {
            return APIResponse.success(service.finish(t, request.getAttemptId()));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }
}
