package com.shuowen.yuzong.user.service;

import com.shuowen.yuzong.user.data.mapper.UserTokenMapper;
import com.shuowen.yuzong.user.data.model.UserTokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 数据库版 Token 存储（默认实现）。
 * <p>
 * 通过配置 {@code token.store.type=db} 激活；缺省配置时也使用本实现。
 * 数据落在表 {@code NC.user_token}，所有读写均依赖 MyBatis 的 {@link UserTokenMapper}。
 */
@Component
@ConditionalOnProperty (name = "token.store.type", havingValue = "db", matchIfMissing = true)
public class DbTokenStore implements TokenStore
{
    @Autowired
    private UserTokenMapper mapper;

    @Override
    @Transactional (rollbackFor = {Exception.class})
    public void save(String token, String username, long expireSeconds)
    {
        // 单用户登录：先作废该用户已有的旧 Token，再写入新 Token
        mapper.deleteByUsername(username);
        mapper.insert(new UserTokenEntity(
                token,
                username,
                LocalDateTime.now().plusSeconds(expireSeconds),
                null
        ));
    }

    @Override
    public String getUsername(String token)
    {
        // SQL 中已过滤已过期记录，返回 null 即代表 Token 无效
        return mapper.selectUsername(token);
    }

    @Override
    public void deleteByToken(String token)
    {
        mapper.deleteByToken(token);
    }

    @Override
    public void deleteByUsername(String username)
    {
        mapper.deleteByUsername(username);
    }

    @Override
    public void refresh(String token, long expireSeconds)
    {
        // SQL 中仅对未过期 Token 续期，防止"死 token 复活"
        mapper.refresh(token, expireSeconds);
    }
}
