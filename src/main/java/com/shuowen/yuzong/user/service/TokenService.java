package com.shuowen.yuzong.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Token 管理服务。
 * <p>
 * 存储介质通过 {@link TokenStore} 抽象，由配置 {@code token.store.type}
 * （db / redis）决定走数据库还是 Redis，业务逻辑与存储介质无关。
 */
@Service
public class TokenService
{
    @Autowired
    private TokenStore tokenStore;

    private static final long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60; // 单位：秒，即七天

    /**
     * 生成并保存Token
     */
    public String generateAndSaveToken(String username)
    {
        // 生成Token
        String token = UUID.randomUUID().toString().replace("-", "");
        // 交给存储层保存；同一用户可同时持有多个有效 Token
        tokenStore.save(token, username, TOKEN_EXPIRE_TIME);
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
        return tokenStore.getUsername(token);
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
        if (token == null || token.trim().isEmpty()) return;

        tokenStore.deleteByToken(token);
    }

    /**
     * 刷新Token过期时间
     */
    public void refreshToken(String token)
    {
        if (validateToken(token))
        {
            tokenStore.refresh(token, TOKEN_EXPIRE_TIME);
        }
    }

    /**
     * 强制用户下线（管理员功能）
     */
    public void forceLogout(String username)
    {
        tokenStore.deleteByUsername(username);
    }
}
