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
        UserEntity u = user.checkIdentityByUsername(username, password);
        return APIResponse.success(token.generateAndSaveToken(u.getUsername()));
    }

    @RequestMapping("/login-by-phone")
    public APIResponse<String> loginByPhone(String phone, String password)
    {
        UserEntity u = user.checkIdentityByPhone(phone, password);
        return APIResponse.success(token.generateAndSaveToken(u.getUsername()));
    }

    @RequestMapping("/login-by-code")
    public APIResponse<String> loginByCode(String phone, String code)
    {
        verificationCode.verifyLoginCode(phone, code);
        UserEntity u = user.loginOrRegisterByPhoneCode(phone);
        return APIResponse.success(token.generateAndSaveToken(u.getUsername()));
    }

    @RequestMapping ("/logout")
    public APIResponse<Void> logout(String t)
    {
        token.removeToken(t);
        return APIResponse.success();
    }

    @RequestMapping ("/check-auth")
    public APIResponse<Void> checkAuth(String t)
    {
        user.getUserByToken(t);
        token.refreshToken(t);  // 每次验证时刷新Token过期时间
        return APIResponse.success();
    }

    @RequestMapping ("/update-username")
    public APIResponse<Void> changeUsername(String t, String newUsername)
    {
        user.updateUsername(t, newUsername);
        return APIResponse.success();
    }

    @RequestMapping ("/update-password")
    public APIResponse<Void> updatePassword(String t, String oldPassword, String newPassword)
    {
        user.updatePassword(t, oldPassword, newPassword);
        return APIResponse.success();
    }

    // @RequestMapping ("/create-user")
    public APIResponse<Void> createUser(String username, String password)
    {
        user.createUser(username, password);
        return APIResponse.success();
    }

    @RequestMapping("/me")
    public APIResponse<UserProfileEntity> me(String t)
    {
        return APIResponse.success(user.getUserProfileByToken(t));
    }
}
