package com.shuowen.yuzong.user.service;

import com.shuowen.yuzong.user.data.mapper.UserMapper;
import com.shuowen.yuzong.user.data.domain.Authority;
import com.shuowen.yuzong.user.data.model.UserEntity;
import com.shuowen.yuzong.user.data.model.UserProfileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.shuowen.yuzong.user.utils.PasswordUtil.encodePassword;
import static com.shuowen.yuzong.user.utils.PasswordUtil.isPasswordEqual;
import static com.shuowen.yuzong.util.ext.other.NullTool.assertNotNull;

@Service
@Transactional (rollbackFor = {Exception.class})
public class UserService
{
    private static final String DEFAULT_AUTHORITY = "[]";
    private static final AtomicInteger GUEST_COUNTER = new AtomicInteger(1000);

    @Autowired
    private UserMapper user;

    @Autowired
    private TokenService token;

    /**
     * 检查用户名、密码是否对应，用于登陆
     */
    public UserEntity checkIdentityByUsername(String username, String password)
    {
        var u = assertNotNull(user.getUserByName(username), new NoSuchElementException("用户不存在"));
        if (!isPasswordEqual(password, u.getPassword()))
            throw new IllegalArgumentException("用户名或者密码错误");
        return u;
    }

    public UserEntity checkIdentityByPhone(String phone, String password)
    {
        var u = assertNotNull(user.getUserByPhone(phone), new NoSuchElementException("手机号不存在"));
        if (!isPasswordEqual(password, u.getPassword()))
            throw new IllegalArgumentException("手机号或者密码错误");
        return u;
    }

    public UserEntity loginOrRegisterByPhoneCode(String phone)
    {
        var existing = user.getUserByPhone(phone);
        if (existing != null)
        {
            return existing;
        }

        var username = generateGuestUsername(phone);
        var newUser = new UserEntity(null, username, phone, encodePassword(generateTemporaryPassword(phone)), DEFAULT_AUTHORITY);
        user.insertUser(newUser);
        return assertNotNull(user.getUserByPhone(phone), new NoSuchElementException("手机号注册失败"));
    }

    /**
     * 通过令牌获得用户数据
     */
    public UserEntity getUserByToken(String t)
    {
        var name = assertNotNull(token.getUsernameByToken(t), new NoSuchElementException("登陆状态无效"));
        return assertNotNull(user.getUserByName(name), new NoSuchElementException("用户不存在"));
    }

    public void createUser(String username, String password)
    {
        if (user.getUserByName(username) != null) throw new IllegalArgumentException("用户名重复");
        user.insertUser(
                new UserEntity(null, username, null, encodePassword(password), DEFAULT_AUTHORITY)
        );
    }

    public void updateUsername(String t, String newUsername)
    {
        var u = getUserByToken(t);
        if (user.getUserByName(newUsername) != null) throw new IllegalArgumentException("用户名重复");
        u.setUsername(newUsername);
        user.updateUsername(u);
        token.removeToken(t);
    }


    public void updatePassword(String t, String oldPassword, String newPassword)
    {
        var u = getUserByToken(t);

        if (isPasswordEqual(oldPassword,u.getPassword()))
        {
            u.setPassword(encodePassword(newPassword));
            user.updatePassword(u);
            token.removeToken(t);
        }
        else throw new IllegalArgumentException("密码错误");
    }

    public UserEntity getUserByPhone(String phone)
    {
        return user.getUserByPhone(phone);
    }

    public UserProfileEntity getUserProfileByToken(String t)
    {
        var u = getUserByToken(t);
        return new UserProfileEntity(
                u.getId(),
                u.getUsername(),
                u.getPhone(),
                u.getAuthority(),
                hasAdminAuthority(u.getAuthority())
        );
    }

    public boolean hasAdminAuthority(String authority)
    {
        if (authority == null || authority.trim().isEmpty())
        {
            return false;
        }
        if (authority.toLowerCase().contains(Authority.ADMIN.name().toLowerCase()))
        {
            return true;
        }
        return authority.toLowerCase().contains("\"admin\"")
                || authority.toLowerCase().contains("'admin'")
                || Authority.of(authority) == Authority.ADMIN;
    }

    private String generateGuestUsername(String phone)
    {
        String suffix = phone == null ? String.valueOf(GUEST_COUNTER.incrementAndGet()) : phone.substring(Math.max(0, phone.length() - 4));
        String candidate = "user_" + suffix;
        while (user.getUserByName(candidate) != null)
        {
            candidate = "user_" + suffix + "_" + GUEST_COUNTER.incrementAndGet();
        }
        return candidate;
    }

    private String generateTemporaryPassword(String phone)
    {
        return "phone-login:" + phone + ":" + UUID.randomUUID();
    }
}
