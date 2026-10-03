package com.shuowen.yuzong.study.wordcard;

import com.shuowen.yuzong.study.wordcard.data.ChapterWordCardAssignment;
import com.shuowen.yuzong.study.wordcard.data.ChapterWordCardGroup;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study/curriculum/manage/{dialect}/word-cards")
public class ChapterWordCardController
{
    private final ChapterWordCardService service;

    public ChapterWordCardController(ChapterWordCardService service) { this.service = service; }

    @GetMapping
    public APIResponse<List<ChapterWordCardGroup>> getBoard(@PathVariable Dialect dialect)
    {
        try { return APIResponse.success(service.getBoard(dialect)); }
        catch (Exception e) { return APIResponse.failure(e.getMessage()); }
    }

    @PutMapping
    public APIResponse<List<ChapterWordCardGroup>> saveBoard(
            @PathVariable Dialect dialect, @RequestBody List<ChapterWordCardAssignment> assignments)
    {
        try { return APIResponse.success(service.saveBoard(assignments, dialect)); }
        catch (Exception e) { return APIResponse.failure(e.getMessage()); }
    }

    @PostMapping("/{chapterId}")
    public APIResponse<WordCardUpdate> createCard(
            @PathVariable Dialect dialect, @PathVariable Integer chapterId, @RequestBody WordCardUpdate update)
    {
        try { return APIResponse.success(service.createCard(chapterId, update, dialect)); }
        catch (Exception e) { return APIResponse.failure(e.getMessage()); }
    }

    @PostMapping("/edit")
    public APIResponse<WordCardUpdate> editCard(
            @PathVariable Dialect dialect, @RequestBody WordCardUpdate update)
    {
        try { return APIResponse.success(service.editCard(update, dialect)); }
        catch (Exception e) { return APIResponse.failure(e.getMessage()); }
    }

    @DeleteMapping("/{cardId}")
    public APIResponse<Void> deleteCard(@PathVariable Dialect dialect, @PathVariable Integer cardId)
    {
        try { service.deleteCard(cardId, dialect); return APIResponse.success(); }
        catch (Exception e) { return APIResponse.failure(e.getMessage()); }
    }
}
