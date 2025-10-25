package com.shuowen.yuzong.service;

import com.shuowen.yuzong.Tool.redis.RedisTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService
{

    @Autowired
    private RedisTool redisTool;

    // Token过期时间 - 7天
    private static final long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60; // 秒
    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_TOKEN_PREFIX = "user_token:";

    /**
     * 生成并保存Token
     */
    public String generateAndSaveToken(String username)
    {
        // 生成Token
        String token = UUID.randomUUID().toString().replace("-", "");

        // 保存Token -> 用户的映射
        String tokenKey = TOKEN_PREFIX + token;
        redisTool.set(tokenKey, username, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        // 保存用户 -> Token的映射（用于单用户登录）
        String userTokenKey = USER_TOKEN_PREFIX + username;
        String oldToken = (String) redisTool.get(userTokenKey);

        // 如果用户已有Token，删除旧的
        if (oldToken != null)
        {
            redisTool.del(TOKEN_PREFIX + oldToken);
        }

        // 保存新的用户Token映射
        redisTool.set(userTokenKey, token, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        return token;
    }

    /**
     * 根据Token获取用户名
     */
    public String getUsernameByToken(String token)
    {
        if (token == null || token.trim().isEmpty())
        {
            return null;
        }
        String tokenKey = TOKEN_PREFIX + token;
        return (String) redisTool.get(tokenKey);
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token)
    {
        String username = getUsernameByToken(token);
        return username != null && !username.trim().isEmpty();
    }

    /**
     * 删除Token（退出登录）
     */
    public void removeToken(String token)
    {
        if (token == null || token.trim().isEmpty())
        {
            return;
        }

        String tokenKey = TOKEN_PREFIX + token;
        String username = (String) redisTool.get(tokenKey);

        if (username != null)
        {
            // 删除Token映射
            redisTool.del(tokenKey);
            // 删除用户Token映射
            String userTokenKey = USER_TOKEN_PREFIX + username;
            redisTool.del(userTokenKey);
        }
    }

    /**
     * 刷新Token过期时间
     */
    public void refreshToken(String token)
    {
        if (validateToken(token))
        {
            String tokenKey = TOKEN_PREFIX + token;
            String username = (String) redisTool.get(tokenKey);

            if (username != null)
            {
                // 刷新Token过期时间
                redisTool.expire(tokenKey, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

                // 刷新用户Token映射的过期时间
                String userTokenKey = USER_TOKEN_PREFIX + username;
                redisTool.expire(userTokenKey, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * 强制用户下线（管理员功能）
     */
    public void forceLogout(String username)
    {
        String userTokenKey = USER_TOKEN_PREFIX + username;
        String token = (String) redisTool.get(userTokenKey);

        if (token != null)
        {
            removeToken(token);
        }
    }
}