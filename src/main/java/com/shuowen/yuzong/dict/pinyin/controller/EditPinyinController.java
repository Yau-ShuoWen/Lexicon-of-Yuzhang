package com.shuowen.yuzong.dict.pinyin.controller;

import com.shuowen.yuzong.dict.pinyin.PinyinService;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.shuowen.yuzong.dict.pinyin.PinyinDocumentService;

@RestController
@RequestMapping ("/api/edit/pinyin")
public class EditPinyinController
{
    @Autowired
    PinyinService py;

    @Autowired
    PinyinDocumentService documents;

    @GetMapping ("/legacy/{d}")
    public APIResponse<Map<String, Object>> legacy(@PathVariable Dialect d)
    {
        return APIResponse.success(documents.legacy(d));
    }

    @PostMapping ("/document/{d}")
    public APIResponse<Void> saveDocument(@PathVariable Dialect d, @RequestBody JsonNode document)
    {
        documents.save(d, document);
        return APIResponse.success();
    }

    @GetMapping ("/filter/{d}")
    public APIResponse<List<String>> filter(@PathVariable Dialect d)
    {
        return APIResponse.success(py.getKey(d));
    }

    @GetMapping ("/get-note/{d}")
    public APIResponse<ScTcText> getNote(@PathVariable Dialect d, @RequestParam String key)
    {
        return APIResponse.success(py.getNote(d, key));
    }

    @PostMapping ("/update-note/{d}")
    public APIResponse<Void> updateNote(@PathVariable Dialect d,
                           @RequestParam String key, @RequestBody ScTcText note)
    {
        py.updateNote(d, key, note);
        return APIResponse.success();
    }
}
