package com.shuowen.yuzong.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shuowen.yuzong.user.service.UserSettingService;
import com.shuowen.yuzong.util.tuple.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/settings")
@RequiredArgsConstructor
public class UserSettingController
{
    private final UserSettingService settings;

    @GetMapping
    public APIResponse<UserSettingService.Values> get(@RequestHeader(value = "X-Auth-Token", required = false) String token)
    {
        return APIResponse.success(settings.get(token));
    }

    @PatchMapping
    public APIResponse<UserSettingService.Values> update(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @RequestBody JsonNode body)
    {
        if (body == null || !body.isObject() || body.size() != 2
                || !body.has("lacLegacyPinyinReadWrite") || !body.get("lacLegacyPinyinReadWrite").isBoolean()
                || !body.has("version") || !body.get("version").isIntegralNumber()
                || !body.get("version").canConvertToLong()
                || body.get("version").longValue() < 0)
            throw new IllegalArgumentException("设置内容无效");
        var change = new UserSettingService.Change(
                body.get("lacLegacyPinyinReadWrite").booleanValue(),
                body.get("version").longValue());
        return APIResponse.success(settings.update(token, change));
    }
}
