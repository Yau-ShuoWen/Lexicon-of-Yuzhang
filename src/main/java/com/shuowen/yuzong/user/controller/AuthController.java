package com.shuowen.yuzong.user.controller;

import com.shuowen.yuzong.user.service.TokenService;
import com.shuowen.yuzong.user.service.UserService;
import com.shuowen.yuzong.user.service.VerificationCodeService;
import com.shuowen.yuzong.user.data.model.UserEntity;
import com.shuowen.yuzong.user.data.model.UserProfileEntity;
import com.shuowen.yuzong.util.tuple.APIResponse;
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

    @Autowired
    private VerificationCodeService verificationCode;


    @RequestMapping ("/login")
    public APIResponse<String> login(String username, String password)
    {
        try
        {
            UserEntity u = user.checkIdentityByUsername(username, password);
            return APIResponse.success(token.generateAndSaveToken(u.getUsername()));
        } catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @RequestMapping("/login-by-phone")
    public APIResponse<String> loginByPhone(String phone, String password)
    {
        try
        {
            UserEntity u = user.checkIdentityByPhone(phone, password);
            return APIResponse.success(token.generateAndSaveToken(u.getUsername()));
        } catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @RequestMapping("/login-by-code")
    public APIResponse<String> loginByCode(String phone, String code)
    {
        try
        {
            if (phone == null || phone.trim().isEmpty())
                throw new IllegalArgumentException("手机号不能为空");
            if (code == null || code.trim().isEmpty())
                throw new IllegalArgumentException("验证码不能为空");

            verificationCode.verifyLoginCode(phone.trim(), code.trim());
            UserEntity u = user.loginOrRegisterByPhoneCode(phone.trim());
            return APIResponse.success(token.generateAndSaveToken(u.getUsername()));
        } catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
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
            user.getUserByToken(t);
            token.refreshToken(t);  // 每次验证时刷新Token过期时间
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure("登陆状态无效。Invalid login status.");
        }
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

    // @RequestMapping ("/create-user")
    public APIResponse<Void> createUser(String username, String password)
    {
        try
        {
            user.createUser(username, password);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @RequestMapping("/me")
    public APIResponse<UserProfileEntity> me(String t)
    {
        try
        {
            return APIResponse.success(user.getUserProfileByToken(t));
        }
        catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }
}
