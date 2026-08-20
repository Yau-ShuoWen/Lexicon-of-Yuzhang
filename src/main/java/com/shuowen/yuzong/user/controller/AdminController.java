package com.shuowen.yuzong.user.controller;

import com.shuowen.yuzong.user.service.UserService;
import com.shuowen.yuzong.user.service.TokenService;
import com.shuowen.yuzong.user.data.model.UserProfileEntity;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController
{
    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @RequestMapping("/dashboard")
    public APIResponse<Map<String, Object>> dashboard(String t)
    {
        try
        {
            UserProfileEntity profile = userService.getUserProfileByToken(t);
            if (!profile.isAdmin())
            {
                return APIResponse.failure("管理员权限不足");
            }

            return APIResponse.success(Map.of(
                    "profile", profile,
                    "modules", List.of(
                            "用户管理",
                            "验证码审计",
                            "权限管理",
                            "登录记录",
                            "内容审核"
                    )
            ));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @RequestMapping("/logout")
    public APIResponse<Void> forceLogout(String username)
    {
        try
        {
            tokenService.forceLogout(username);
            return APIResponse.success();
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }
}
