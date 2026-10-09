package com.shuowen.yuzong.ysw.controller;

import com.shuowen.yuzong.user.data.domain.AuthorityCode;
import com.shuowen.yuzong.user.service.UserService;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.HTTPCode;
import com.shuowen.yuzong.util.version.TextDiffRequest;
import com.shuowen.yuzong.util.version.TextDiffResponse;
import com.shuowen.yuzong.util.version.TextDiffService;
import com.shuowen.yuzong.ysw.data.dto.diary.DiaryCreateRequest;
import com.shuowen.yuzong.ysw.data.dto.diary.DiaryEditData;
import com.shuowen.yuzong.ysw.data.dto.diary.DiaryEditRequest;
import com.shuowen.yuzong.ysw.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/diary/edit")
public class DiaryEditController
{
    @Autowired
    private DiaryService diaryService;

    @Autowired
    private UserService userService;

    @Autowired
    private TextDiffService textDiffService;

    @GetMapping("/{id}")
    public APIResponse<DiaryEditData> getForEdit(
            @PathVariable Integer id,
            @RequestParam(required = false) String t,
            @RequestHeader(value = "X-Auth-Token", required = false) String headerToken)
    {
        try
        {
            requireEditPermission(t, headerToken);
            return APIResponse.success(diaryService.getForEdit(id));
        }
        catch (Exception e)
        {
            return authFailure(e);
        }
    }

    @PostMapping
    public APIResponse<DiaryEditData> create(
            @RequestBody DiaryCreateRequest request,
            @RequestParam(required = false) String t,
            @RequestHeader(value = "X-Auth-Token", required = false) String headerToken)
    {
        try
        {
            requireEditPermission(t, headerToken);
            return APIResponse.success(diaryService.createForEdit(request));
        }
        catch (Exception e)
        {
            return authFailure(e);
        }
    }

    @PutMapping("/{id}")
    public APIResponse<DiaryEditData> updateForEdit(
            @PathVariable Integer id,
            @RequestBody DiaryEditRequest request,
            @RequestParam(required = false) String t,
            @RequestHeader(value = "X-Auth-Token", required = false) String headerToken)
    {
        try
        {
            requireEditPermission(t, headerToken);
            return APIResponse.success(diaryService.updateForEdit(id, request));
        }
        catch (Exception e)
        {
            return authFailure(e);
        }
    }

    @PostMapping("/diff")
    public APIResponse<TextDiffResponse> compare(
            @RequestBody TextDiffRequest request,
            @RequestParam(required = false) String t,
            @RequestHeader(value = "X-Auth-Token", required = false) String headerToken)
    {
        try
        {
            requireEditPermission(t, headerToken);
            return APIResponse.success(textDiffService.compare(request));
        }
        catch (Exception e)
        {
            return authFailure(e);
        }
    }

    private void requireEditPermission(String queryToken, String headerToken)
    {
        String token = queryToken == null || queryToken.trim().isEmpty() ? headerToken : queryToken;
        if (token == null || token.trim().isEmpty())
        {
            throw new NoSuchElementException("请先登录");
        }

        var user = userService.getUserByToken(token);
        if (!userService.hasPermission(user.getAuthority(), AuthorityCode.DIARY_EDIT))
        {
            throw new SecurityException("没有日记编辑权限");
        }
    }

    private <T> APIResponse<T> authFailure(Exception exception)
    {
        if (exception instanceof NoSuchElementException)
        {
            return APIResponse.failure(HTTPCode.NotLogin, exception.getMessage());
        }
        if (exception instanceof SecurityException)
        {
            return APIResponse.failure(HTTPCode.NoPermission, exception.getMessage());
        }
        return APIResponse.failure(exception.getMessage());
    }
}
