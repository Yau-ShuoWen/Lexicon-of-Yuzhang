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
    public APIResponse<List<WordCardPractice>> getWordCards(
            @PathVariable Language language,
            @PathVariable Dialect dialect
    )
    {
        return APIResponse.success(service.getRandomCards(dialect, language));
    }

    @GetMapping("/edit/{dialect}")
    public APIResponse<List<WordCardUpdate>> getAllForEdit(@PathVariable Dialect dialect)
    {
        return APIResponse.success(editService.findAll(dialect));
    }

    @GetMapping("/edit/{dialect}/{id}")
    public APIResponse<WordCardUpdate> getForEdit(
            @PathVariable Dialect dialect, @PathVariable Integer id
    )
    {
        return APIResponse.success(editService.findById(id, dialect));
    }

    @PostMapping("/edit/{dialect}")
    public APIResponse<Void> saveForEdit(
            @PathVariable Dialect dialect, @RequestBody WordCardUpdate update
    )
    {
        editService.save(update, dialect);
        return APIResponse.success();
    }

    @DeleteMapping("/edit/{dialect}/{id}")
    public APIResponse<Void> deleteForEdit(
            @PathVariable Dialect dialect, @PathVariable Integer id
    )
    {
        editService.delete(id, dialect);
        return APIResponse.success();
    }
}
