package com.shuowen.yuzong.user.controller;

import com.shuowen.yuzong.user.data.model.UserVerificationCodeEntity;
import com.shuowen.yuzong.user.service.VerificationCodeService;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/code")
public class VerificationCodeController
{
    @Autowired
    private VerificationCodeService codeService;

    @RequestMapping("/create")
    public APIResponse<UserVerificationCodeEntity> create(String phone)
    {
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("手机号不能为空");
        return APIResponse.success(codeService.createLoginCode(phone.trim()));
    }

    @RequestMapping("/latest")
    public APIResponse<UserVerificationCodeEntity> latest(String phone)
    {
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("手机号不能为空");
        return APIResponse.success(codeService.getLatestLoginCode(phone.trim()));
    }

    @RequestMapping("/recent")
    public APIResponse<List<UserVerificationCodeEntity>> recent(String phone, Integer limit)
    {
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("手机号不能为空");
        return APIResponse.success(codeService.listRecentLoginCodes(phone.trim(), limit == null ? 5 : limit));
    }

    @RequestMapping("/verify")
    public APIResponse<Boolean> verify(String phone, String code)
    {
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("手机号不能为空");
        if (code == null || code.trim().isEmpty())
            throw new IllegalArgumentException("验证码不能为空");
        codeService.verifyLoginCode(phone.trim(), code.trim());
        return APIResponse.success(true);
    }
}
