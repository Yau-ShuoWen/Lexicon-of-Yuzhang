package com.shuowen.yuzong.study.controller;

import com.shuowen.yuzong.study.data.domain.WordCardUpdate;
import com.shuowen.yuzong.study.data.dto.WordCardPractice;
import com.shuowen.yuzong.study.service.WordCardEditService;
import com.shuowen.yuzong.study.service.WordCardService;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study")
public class StudyController
{
    @Autowired
    private WordCardService service;

    @Autowired
    private WordCardEditService editService;

    @GetMapping("/word-cards/{language}/{dialect}")
    public List<WordCardPractice> getWordCards(
            @PathVariable Language language,
            @PathVariable Dialect dialect
    )
    {
        return service.getRandomCards(dialect, language);
    }

    @GetMapping("/edit/{dialect}")
    public APIResponse<List<WordCardUpdate>> getAllForEdit(@PathVariable Dialect dialect)
    {
        try {
            return APIResponse.success(editService.findAll(dialect));
        } catch (Exception e) {
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping("/edit/{dialect}/{id}")
    public APIResponse<WordCardUpdate> getForEdit(
            @PathVariable Dialect dialect, @PathVariable Integer id
    )
    {
        try {
            return APIResponse.success(editService.findById(id, dialect));
        } catch (Exception e) {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/edit/{dialect}")
    public APIResponse<Void> saveForEdit(
            @PathVariable Dialect dialect, @RequestBody WordCardUpdate update
    )
    {
        try {
            editService.save(update, dialect);
            return APIResponse.success();
        } catch (Exception e) {
            return APIResponse.failure(e.getMessage());
        }
    }

    @DeleteMapping("/edit/{dialect}/{id}")
    public APIResponse<Void> deleteForEdit(
            @PathVariable Dialect dialect, @PathVariable Integer id
    )
    {
        try {
            editService.delete(id, dialect);
            return APIResponse.success();
        } catch (Exception e) {
            return APIResponse.failure(e.getMessage());
        }
    }
}
