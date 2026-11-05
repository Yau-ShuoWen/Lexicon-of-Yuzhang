package com.shuowen.yuzong.ysw.diary.controller;

import com.shuowen.yuzong.user.data.domain.AuthorityCode;
import com.shuowen.yuzong.user.service.UserService;
import com.shuowen.yuzong.util.err.APIException;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.HTTPCode;
import com.shuowen.yuzong.util.version.TextDiffRequest;
import com.shuowen.yuzong.util.version.TextDiffResponse;
import com.shuowen.yuzong.util.version.TextDiffService;
import com.shuowen.yuzong.ysw.diary.data.DiaryCreateRequest;
import com.shuowen.yuzong.ysw.diary.data.DiaryEditData;
import com.shuowen.yuzong.ysw.diary.data.DiaryEditRequest;
import com.shuowen.yuzong.ysw.diary.service.DiaryService;
import com.shuowen.yuzong.media.image.ImageService;
import com.shuowen.yuzong.media.image.ImageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/diary/edit")
public class DiaryEditController
{
    @Autowired
    private DiaryService diaryService;

    @Autowired
    private ImageService imageService;

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
        requireEditPermission(t, headerToken);
        return APIResponse.success(diaryService.getForEdit(id));
    }

    @PostMapping("/{id}/image")
    public APIResponse<ImageVO> uploadImage(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String t,
            @RequestHeader(value = "X-Auth-Token", required = false) String headerToken) throws IOException
    {
        requireEditPermission(t, headerToken);
        diaryService.getForEdit(id);
        return APIResponse.success(imageService.uploadDiary(file, id));
    }

    @GetMapping("/{id}/image/{imageId}")
    public APIResponse<ImageVO> getImage(
            @PathVariable Integer id,
            @PathVariable Long imageId,
            @RequestParam(required = false) String t,
            @RequestHeader(value = "X-Auth-Token", required = false) String headerToken)
    {
        requireEditPermission(t, headerToken);
        diaryService.getForEdit(id);
        return APIResponse.success(imageService.getDiaryImage(imageId, id));
    }

    @PostMapping
    public APIResponse<DiaryEditData> create(
            @RequestBody DiaryCreateRequest request,
            @RequestParam(required = false) String t,
            @RequestHeader(value = "X-Auth-Token", required = false) String headerToken)
    {
        requireEditPermission(t, headerToken);
        return APIResponse.success(diaryService.createForEdit(request));
    }

    @PutMapping("/{id}")
    public APIResponse<DiaryEditData> updateForEdit(
            @PathVariable Integer id,
            @RequestBody DiaryEditRequest request,
            @RequestParam(required = false) String t,
            @RequestHeader(value = "X-Auth-Token", required = false) String headerToken)
    {
        requireEditPermission(t, headerToken);
        return APIResponse.success(diaryService.updateForEdit(id, request));
    }

    @PostMapping("/diff")
    public APIResponse<TextDiffResponse> compare(
            @RequestBody TextDiffRequest request,
            @RequestParam(required = false) String t,
            @RequestHeader(value = "X-Auth-Token", required = false) String headerToken)
    {
        requireEditPermission(t, headerToken);
        return APIResponse.success(textDiffService.compare(request));
    }

    private void requireEditPermission(String queryToken, String headerToken)
    {
        String token = queryToken == null || queryToken.trim().isEmpty() ? headerToken : queryToken;
        if (token == null || token.trim().isEmpty())
        {
            throw new APIException(HTTPCode.NotLogin, "请先登录");
        }

        var user = userService.getUserByToken(token);
        if (!userService.hasPermission(user.getAuthority(), AuthorityCode.DIARY_EDIT))
        {
            throw new APIException(HTTPCode.NoPermission, "没有日记编辑权限");
        }
    }
}
