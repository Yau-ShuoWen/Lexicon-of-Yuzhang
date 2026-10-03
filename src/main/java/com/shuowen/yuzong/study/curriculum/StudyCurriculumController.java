package com.shuowen.yuzong.study.curriculum;

import com.shuowen.yuzong.study.curriculum.data.StudyLevelUpdate;
import com.shuowen.yuzong.study.curriculum.data.StudyChapterCatalog;
import com.shuowen.yuzong.study.curriculum.data.StudyChapterUpdate;
import com.shuowen.yuzong.study.curriculum.data.StudyPartCatalog;
import com.shuowen.yuzong.study.curriculum.data.StudyPartUpdate;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习目录接口。当前先提供最上层“部分”的读取与整体保存。
 *
 * <p>manage 路径由 AdminAccessFilter 统一保护；公开目录只返回已发布内容。</p>
 */
@RestController
@RequestMapping("/api/study/curriculum")
public class StudyCurriculumController
{
    private final StudyCurriculumService service;

    public StudyCurriculumController(StudyCurriculumService service)
    {
        this.service = service;
    }

    @GetMapping("/{language}/{dialect}")
    public APIResponse<List<StudyPartCatalog>> catalog(
            @PathVariable Language language,
            @PathVariable Dialect dialect)
    {
        try
        {
            return APIResponse.success(service.getCatalog(language, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping("/manage/{dialect}/parts")
    public APIResponse<List<StudyPartCatalog>> findParts(@PathVariable Dialect dialect)
    {
        try
        {
            return APIResponse.success(service.findAllForManage(dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping("/manage/{dialect}/part/{id}")
    public APIResponse<StudyPartUpdate> findPart(
            @PathVariable Dialect dialect, @PathVariable Integer id)
    {
        try
        {
            return APIResponse.success(service.findPart(id, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/manage/{dialect}/part")
    public APIResponse<StudyPartUpdate> savePart(
            @PathVariable Dialect dialect,
            @RequestBody StudyPartUpdate update)
    {
        try
        {
            return APIResponse.success(service.savePart(update, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PutMapping("/manage/{dialect}/parts")
    public APIResponse<List<StudyPartCatalog>> saveParts(
            @PathVariable Dialect dialect,
            @RequestBody List<StudyPartUpdate> updates)
    {
        try
        {
            return APIResponse.success(service.saveParts(updates, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @DeleteMapping("/manage/{dialect}/parts/{id}")
    public APIResponse<Void> deletePart(@PathVariable Dialect dialect, @PathVariable Integer id)
    {
        try
        {
            service.deletePart(id, dialect);
            return APIResponse.success();
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping("/manage/{dialect}/parts/{partId}/chapters")
    public APIResponse<List<StudyChapterCatalog>> findChapters(
            @PathVariable Dialect dialect, @PathVariable Integer partId)
    {
        try
        {
            return APIResponse.success(service.findChapters(partId, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PutMapping("/manage/{dialect}/parts/{partId}/chapters")
    public APIResponse<List<StudyChapterCatalog>> saveChapters(
            @PathVariable Dialect dialect,
            @PathVariable Integer partId,
            @RequestBody List<StudyChapterUpdate> updates)
    {
        try
        {
            return APIResponse.success(service.saveChapters(partId, updates, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @DeleteMapping("/manage/{dialect}/parts/{partId}/chapters/{chapterId}")
    public APIResponse<Void> deleteChapter(
            @PathVariable Dialect dialect,
            @PathVariable Integer partId,
            @PathVariable Integer chapterId)
    {
        try
        {
            service.deleteChapter(partId, chapterId, dialect);
            return APIResponse.success();
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping("/manage/{dialect}/levels")
    public APIResponse<List<StudyLevelUpdate>> findLevels(
            @PathVariable Dialect dialect, @RequestParam Integer partId)
    {
        try
        {
            return APIResponse.success(service.findLevels(partId, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping("/manage/{dialect}/level/{id}")
    public APIResponse<StudyLevelUpdate> findLevel(
            @PathVariable Dialect dialect, @PathVariable Integer id)
    {
        try
        {
            return APIResponse.success(service.findLevel(id, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/manage/{dialect}/level")
    public APIResponse<StudyLevelUpdate> saveLevel(
            @PathVariable Dialect dialect,
            @RequestBody StudyLevelUpdate update)
    {
        try
        {
            return APIResponse.success(service.saveLevel(update, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }
}
