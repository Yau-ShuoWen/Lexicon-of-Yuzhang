package com.shuowen.yuzong.controller.edit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.service.impl.IPA.PinyinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/edit/pinyin")
public class EditPinyinController
{
    @Autowired
    PinyinService py;

    @GetMapping ("/filter/{d}")
    public List<String> filter(@PathVariable Dialect d)
    {
        return py.getKey(d);
    }

    @GetMapping ("/get-note/{d}")
    public ScTcText getNote(@PathVariable Dialect d, @RequestParam String key)
    {
        return py.getNote(d, key);
    }

    @PostMapping ("/update-note/{d}")
    public void updateNote(@PathVariable Dialect d,
                           @RequestParam String key, @RequestBody ScTcText note)
    {
        py.updateNote(d, key, note);
    }
}
