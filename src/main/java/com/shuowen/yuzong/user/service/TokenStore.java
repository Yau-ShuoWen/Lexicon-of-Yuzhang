package com.shuowen.yuzong.user.service;

/**
 * Token 存储抽象。
 * <p>
 * 提供数据库 / Redis 两种实现，通过配置 {@code token.store.type} 切换：
 * <ul>
 *     <li>{@code db}    ：数据库存储（默认，缺省配置时也使用本实现）</li>
 *     <li>{@code redis} ：Redis 存储</li>
 * </ul>
 * 各实现内部保证"单用户登录"（同一用户的新 Token 会使旧 Token 作废）与过期语义一致，
 * 因此上层（TokenService / 控制器）无需感知存储介质。
 */
public interface TokenStore
{
    /**
     * 保存新 Token，并作废该用户已存在的旧 Token（单用户登录）。
     *
     * @param token         新 Token
     * @param username      所属用户
     * @param expireSeconds 有效时长（秒）
     */
    void save(String token, String username, long expireSeconds);

    /**
     * 根据 Token 获取用户名；Token 不存在或已过期时返回 null。
     *
     * @param token 登录 Token
     * @return 用户名；无效则返回 null
     */
    String getUsername(String token);

    /**
     * 删除指定 Token（退出登录）。
     *
     * @param token 登录 Token
     */
    void deleteByToken(String token);

    /**
     * 删除指定用户的全部 Token（强制下线 / 改名 / 改密后作废旧登录）。
     *
     * @param username 用户名
     */
    void deleteByUsername(String username);

    /**
     * 刷新 Token 过期时间（滑动续期）。
     *
     * @param token         登录 Token
     * @param expireSeconds 续期后的有效时长（秒）
     */
    void refresh(String token, long expireSeconds);
}
