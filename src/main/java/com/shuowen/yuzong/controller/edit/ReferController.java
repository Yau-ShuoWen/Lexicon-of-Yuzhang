package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Refer.Citiao;
import com.shuowen.yuzong.data.dto.Refer.CitiaoEdit;
import com.shuowen.yuzong.service.impl.Refer.ReferServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/refer")
public class ReferController
{
    @Autowired
    private ReferServiceImpl r;

    /**
     * 获取词典列表
     */
    @GetMapping ("/get-dictionaries/{lang}")
    public List<Pair<String, String>> getDictionaries(
            @PathVariable String lang
    )
    {
        return r.getDictionaries(lang);
    }

    /**
     * 模糊搜索词条
     */
    @GetMapping ("/search")
    public APIResponse<List<Citiao>> fuzzySearch(
            @RequestParam String keyword,
            @RequestParam String dict)
    {
        try
        {
            return APIResponse.success(r.scopeSearch(keyword, dict));
        } catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    /**
     * 获取词条上下文
     */
    @GetMapping ("/context/{id}")
    public APIResponse<List<Citiao>> getContext(
            @PathVariable Integer id,
            @RequestParam String dict)
    {
        try
        {
            return APIResponse.success(r.getContext(id, dict));
        } catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    /**
     * 批量更新词条（增删改）
     */
    @PostMapping ("/batch-update")
    public APIResponse<Void> batchUpdate(@RequestBody Pair<List<CitiaoEdit>, List<CitiaoEdit>> updates)
    {
        try
        {
            r.batchUpdate(updates);
            return APIResponse.success();
        } catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

}
