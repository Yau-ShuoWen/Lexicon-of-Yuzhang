package com.shuowen.yuzong.user.service;

import com.shuowen.yuzong.user.data.mapper.UserVerificationCodeMapper;
import com.shuowen.yuzong.user.data.model.UserVerificationCodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = {Exception.class})
public class VerificationCodeService
{
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    private static final long EXPIRE_MINUTES = 10;
    private static final String PURPOSE_LOGIN = "login";

    @Autowired
    private UserVerificationCodeMapper mapper;

    public UserVerificationCodeEntity createLoginCode(String phone)
    {
        if (phone == null || phone.trim().isEmpty())
        {
            throw new IllegalArgumentException("手机号不能为空");
        }
        phone = phone.trim();
        mapper.deleteExpired();
        var code = generateCode();
        var entity = new UserVerificationCodeEntity(
                null,
                phone,
                code,
                PURPOSE_LOGIN,
                false,
                LocalDateTime.now().plusMinutes(EXPIRE_MINUTES),
                LocalDateTime.now(),
                null
        );
        mapper.insert(entity);
        return mapper.selectLatestByPhone(phone, PURPOSE_LOGIN);
    }

    public UserVerificationCodeEntity verifyLoginCode(String phone, String code)
    {
        if (phone == null || phone.trim().isEmpty())
        {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (code == null || code.trim().isEmpty())
        {
            throw new IllegalArgumentException("验证码不能为空");
        }
        phone = phone.trim();
        code = code.trim();
        var record = mapper.selectValidByPhoneAndCode(phone, code, PURPOSE_LOGIN);
        if (record == null)
        {
            throw new IllegalArgumentException("验证码无效或已过期");
        }
        mapper.markUsed(record.getId());
        record.setUsed(true);
        record.setUsedAt(LocalDateTime.now());
        return record;
    }

    public UserVerificationCodeEntity getLatestLoginCode(String phone)
    {
        phone = phone == null ? null : phone.trim();
        return mapper.selectLatestByPhone(phone, PURPOSE_LOGIN);
    }

    public List<UserVerificationCodeEntity> listRecentLoginCodes(String phone, int limit)
    {
        phone = phone == null ? null : phone.trim();
        return mapper.listRecentByPhone(phone, Math.max(1, Math.min(limit, 20)));
    }

    public void cleanupExpired()
    {
        mapper.deleteExpired();
    }

    private String generateCode()
    {
        int max = (int) Math.pow(10, CODE_LENGTH);
        int value = RANDOM.nextInt(max);
        return String.format("%0" + CODE_LENGTH + "d", value);
    }
}
