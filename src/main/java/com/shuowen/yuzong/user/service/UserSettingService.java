package com.shuowen.yuzong.user.service;

import com.shuowen.yuzong.user.data.mapper.UserSettingMapper;
import com.shuowen.yuzong.util.err.APIException;
import com.shuowen.yuzong.util.tuple.HTTPCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingService
{
    private final UserService users;
    private final UserSettingMapper settings;

    public record Values(boolean lacLegacyPinyinReadWrite, long version) {}

    public record Change(Boolean lacLegacyPinyinReadWrite, Long version) {}

    public boolean usesLegacyPinyin(String token)
    {
        Integer userId = users.getUserByToken(token).getId();
        return Boolean.parseBoolean(settings.getLegacyPinyin(userId));
    }

    @Transactional(readOnly = true)
    public Values get(String token)
    {
        Integer userId = users.getUserByToken(token).getId();
        Long version = settings.getVersion(userId);
        return new Values(Boolean.parseBoolean(settings.getLegacyPinyin(userId)), version == null ? 0 : version);
    }

    @Transactional
    public Values update(String token, Change values)
    {
        if (values == null || values.lacLegacyPinyinReadWrite() == null || values.version() == null)
            throw new IllegalArgumentException("设置内容不完整");
        Integer userId = users.getUserByToken(token).getId();
        settings.ensureVersion(userId);
        Long currentVersion = settings.lockVersion(userId);
        if (!values.version().equals(currentVersion))
            throw new APIException(HTTPCode.Conflict, "设置已在其他页面更改，请刷新后重试");
        settings.upsertLegacyPinyin(userId, Boolean.toString(values.lacLegacyPinyinReadWrite()));
        settings.incrementVersion(userId);
        return get(token);
    }
}
