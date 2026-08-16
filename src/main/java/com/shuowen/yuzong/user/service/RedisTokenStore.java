package com.shuowen.yuzong.user.service;

import com.shuowen.yuzong.Tool.redis.RedisTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 版 Token 存储（备用实现）。
 * <p>
 * 通过配置 {@code token.store.type=redis} 激活。
 * 保留了原有基于 {@link RedisTool} 的实现逻辑，需要切回 Redis 时仅需修改配置即可。
 */
@Component
@ConditionalOnProperty (name = "token.store.type", havingValue = "redis")
public class RedisTokenStore implements TokenStore
{
    @Autowired
    private RedisTool redisTool;

    /** Token -> 用户 的键前缀 */
    private static final String TOKEN_PREFIX = "token:";
    /** 用户 -> Token 的键前缀（用于单用户登录） */
    private static final String USER_TOKEN_PREFIX = "user_token:";

    @Override
    public void save(String token, String username, long expireSeconds)
    {
        redisTool.set(TOKEN_PREFIX + token, username, expireSeconds, TimeUnit.SECONDS);

        // 单用户登录：作废该用户旧 Token
        String userTokenKey = USER_TOKEN_PREFIX + username;
        String oldToken = (String) redisTool.get(userTokenKey);
        if (oldToken != null) redisTool.del(TOKEN_PREFIX + oldToken);

        redisTool.set(userTokenKey, token, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getUsername(String token)
    {
        return (String) redisTool.get(TOKEN_PREFIX + token);
    }

    @Override
    public void deleteByToken(String token)
    {
        String tokenKey = TOKEN_PREFIX + token;
        String username = (String) redisTool.get(tokenKey);
        if (username != null)
        {
            redisTool.del(tokenKey);
            redisTool.del(USER_TOKEN_PREFIX + username);
        }
    }

    @Override
    public void deleteByUsername(String username)
    {
        String userTokenKey = USER_TOKEN_PREFIX + username;
        String token = (String) redisTool.get(userTokenKey);
        if (token != null)
        {
            deleteByToken(token);
        }
    }

    @Override
    public void refresh(String token, long expireSeconds)
    {
        String tokenKey = TOKEN_PREFIX + token;
        String username = (String) redisTool.get(tokenKey);
        if (username != null)
        {
            redisTool.expire(tokenKey, expireSeconds, TimeUnit.SECONDS);
            redisTool.expire(USER_TOKEN_PREFIX + username, expireSeconds, TimeUnit.SECONDS);
        }
    }
}
