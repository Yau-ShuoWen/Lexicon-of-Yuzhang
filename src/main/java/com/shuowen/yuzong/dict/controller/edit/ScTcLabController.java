package com.shuowen.yuzong.dict.controller.edit;

import com.shuowen.yuzong.util.map.KV;
import com.shuowen.yuzong.util.json.JsonTool;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/edit/sc-tc-lab")
public class ScTcLabController
{
    private static final String KEY = "developer-sc-tc-lab-v1";

    @GetMapping
    public APIResponse<String> read()
    {
        return APIResponse.success(KV.get(KEY));
    }

    @PostMapping
    @Transactional
    public APIResponse<Void> write(@RequestBody ScTcText value)
    {
        KV.set(KEY, JsonTool.toJson(value));
        return APIResponse.success();
    }
}
