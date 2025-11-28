package com.shuowen.yuzong.controller.setting;

import com.shuowen.yuzong.service.impl.Account.TokenService;
import com.shuowen.yuzong.service.impl.Account.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping ("/api")
public class LoginRegisterController
{
    @Autowired
    private UserServiceImpl user;

    @Autowired
    private TokenService tokenService;


    @RequestMapping ("/login")
    public Map<String, Object> hhh(String username, String password)
    {
        Map<String, Object> result = new HashMap<>();

        if (user.check(username, password))
        {
            String token = tokenService.generateAndSaveToken(username);

            result.put("success", true);
            result.put("token", token);
            result.put("username", username);
            result.put("massage", "登陆成功");
        }
        else
        {
            result.put("success", false);
            result.put("massage", "用户名或密码错误");
        }
        return result;
    }

    @RequestMapping ("/logout")
    public Map<String, Object> logout(String token)
    {
        Map<String, Object> result = new HashMap<>();
        tokenService.removeToken(token);
        result.put("success", true);
        result.put("message", "退出成功");
        return result;
    }

    @RequestMapping ("/checkAuth")
    public Map<String, Object> checkAuth(String token)
    {
        Map<String, Object> result = new HashMap<>();
        String username = tokenService.getUsernameByToken(token);

        if (username != null)
        {
            // 每次验证时刷新Token过期时间
            tokenService.refreshToken(token);
            result.put("authenticated", true);
            result.put("username", username);
        }
        else
        {
            result.put("authenticated", false);
        }
        return result;
    }

    @RequestMapping ("/register")

    public Map<String, Object> xxx(String username, String password)
    {
        Map<String, Object> result = new HashMap<>();

        if (user.checkUsernameRepeated(username))
        {
            result.put("success", false);
            result.put("message", "用户名重复");
        }
        else
        {
            user.insertUser(username, password);
            result.put("success", true);
            result.put("message", "注册成功");
        }
        return result;
    }

    /**
     * 获取当前用户信息
     */
    @RequestMapping ("/userInfo")
    public Map<String, Object> getUserInfo(String token)
    {
        Map<String, Object> result = new HashMap<>();
        String username = tokenService.getUsernameByToken(token);

        if (username != null)
        {
            // 这里可以添加获取更多用户信息的逻辑
            result.put("success", true);
            result.put("username", username);
            // 可以添加其他用户信息，如昵称、邮箱等
        }
        else
        {
            result.put("success", false);
            result.put("message", "用户未登录");
        }
        return result;
    }
}
