package com.shuowen.yuzong.user.service;

import com.shuowen.yuzong.util.redis.RedisTool;
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
    /** 用户 -> Token 集合 的键前缀（用于批量下线） */
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    @Override
    public void save(String token, String username, long expireSeconds)
    {
        redisTool.set(TOKEN_PREFIX + token, username, expireSeconds, TimeUnit.SECONDS);
        String userTokensKey = USER_TOKENS_PREFIX + username;
        redisTool.sadd(userTokensKey, token);
        redisTool.expire(userTokensKey, expireSeconds, TimeUnit.SECONDS);
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
            redisTool.srem(USER_TOKENS_PREFIX + username, token);
        }
    }

    @Override
    public void deleteByUsername(String username)
    {
        String userTokensKey = USER_TOKENS_PREFIX + username;
        for (Object token : redisTool.smembers(userTokensKey))
        {
            if (token != null)
            {
                redisTool.del(TOKEN_PREFIX + token);
            }
        }
        redisTool.del(userTokensKey);
    }

    @Override
    public void refresh(String token, long expireSeconds)
    {
        String tokenKey = TOKEN_PREFIX + token;
        String username = (String) redisTool.get(tokenKey);
        if (username != null)
        {
            redisTool.expire(tokenKey, expireSeconds, TimeUnit.SECONDS);
            redisTool.expire(USER_TOKENS_PREFIX + username, expireSeconds, TimeUnit.SECONDS);
        }
    }
}
