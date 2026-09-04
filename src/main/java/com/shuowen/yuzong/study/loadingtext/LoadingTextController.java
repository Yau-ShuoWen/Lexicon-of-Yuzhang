package com.shuowen.yuzong.study.loadingtext;

import com.shuowen.yuzong.study.loadingtext.data.LoadingTextBatchItem;
import com.shuowen.yuzong.study.loadingtext.data.LoadingTextUpdate;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study/loading-text")
public class LoadingTextController
{
    private final LoadingTextService service;

    public LoadingTextController(LoadingTextService service)
    {
        this.service = service;
    }

    @GetMapping("/{language}/{dialect}")
    public APIResponse<String> randomText(
            @PathVariable Language language,
            @PathVariable Dialect dialect)
    {
        try
        {
            return APIResponse.success(service.randomText(language, dialect));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping("/manage")
    public APIResponse<List<LoadingTextUpdate>> findAll()
    {
        try
        {
            return APIResponse.success(service.findAll());
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping("/manage/{id}")
    public APIResponse<LoadingTextUpdate> findById(@PathVariable Integer id)
    {
        try
        {
            return APIResponse.success(service.findById(id));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/manage")
    public APIResponse<Void> save(@RequestBody LoadingTextUpdate edit)
    {
        try
        {
            service.save(edit);
            return APIResponse.success();
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping("/manage/batch")
    public APIResponse<Void> saveBatch(@RequestBody List<LoadingTextBatchItem> items)
    {
        try
        {
            service.saveBatch(items);
            return APIResponse.success();
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }
}
