package com.shuowen.yuzong.controller.setting;

import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.service.impl.Account.TokenService;
import com.shuowen.yuzong.service.impl.Account.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/api/user")
public class AuthController
{
    @Autowired
    private UserService user;

    @Autowired
    private TokenService token;


    @RequestMapping ("/login")
    public APIResponse<String> login(String username, String password)
    {
        return user.checkIdentity(username, password) ?
                APIResponse.success(token.generateAndSaveToken(username)) :
                APIResponse.failure("用户名或密码错误。Username or Password incorrect.");
    }

    @RequestMapping ("/logout")
    public APIResponse<Void> logout(String t)
    {
        try
        {
            token.removeToken(t);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @RequestMapping ("/check-auth")
    public APIResponse<Void> checkAuth(String t)
    {
        try
        {
            if (user.getUserByToken(t).isValid())
            {
                token.refreshToken(t);  // 每次验证时刷新Token过期时间
                return APIResponse.success();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return APIResponse.failure("登陆状态无效。Invalid login status.");
    }

    @RequestMapping ("/update-username")
    public APIResponse<Void> changeUsername(String t, String newUsername)
    {
        try
        {
            user.updateUsername(t, newUsername);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @RequestMapping ("/update-password")
    public APIResponse<Void> updatePassword(String t, String oldPassword, String newPassword)
    {
        try
        {
            user.updatePassword(t, oldPassword, newPassword);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @RequestMapping ("/create-user")
    public APIResponse<Void> createUser(String username)
    {
        try
        {
            user.createUser(username,"123456");
            return APIResponse.success();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }
}
