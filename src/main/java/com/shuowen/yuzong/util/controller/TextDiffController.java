package com.shuowen.yuzong.util.controller;

import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.version.TextDiffRequest;
import com.shuowen.yuzong.util.version.TextDiffResponse;
import com.shuowen.yuzong.util.version.TextDiffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用文本差异工具接口。
 * <p>
 * 这里只负责计算差异，不绑定日记、文章或其他业务模块。
 */
@RestController
@RequestMapping("/api/tool")
public class TextDiffController
{
    @Autowired
    private TextDiffService textDiffService;

    @PostMapping("/string-diff")
    public APIResponse<TextDiffResponse> compare(@RequestBody TextDiffRequest request)
    {
        return APIResponse.success(textDiffService.compare(request));
    }
}
