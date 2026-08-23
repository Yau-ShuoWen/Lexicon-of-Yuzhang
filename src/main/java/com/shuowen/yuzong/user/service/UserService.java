package com.shuowen.yuzong.user.service;

import com.shuowen.yuzong.user.data.mapper.UserMapper;
import com.shuowen.yuzong.user.data.domain.Authority;
import com.shuowen.yuzong.user.data.domain.AuthorityCode;
import com.shuowen.yuzong.user.data.model.UserEntity;
import com.shuowen.yuzong.user.data.model.UserProfileEntity;
import com.shuowen.yuzong.util.json.JsonTool;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
        username = normalize(username);
        var u = assertNotNull(user.getUserByName(username), new NoSuchElementException("用户不存在"));
        if (!isPasswordEqual(password, u.getPassword()))
            throw new IllegalArgumentException("用户名或者密码错误");
        return u;
    }

    public UserEntity checkIdentityByPhone(String phone, String password)
    {
        phone = normalize(phone);
        var u = assertNotNull(user.getUserByPhone(phone), new NoSuchElementException("手机号不存在"));
        if (!isPasswordEqual(password, u.getPassword()))
            throw new IllegalArgumentException("手机号或者密码错误");
        return u;
    }

    public UserEntity loginOrRegisterByPhoneCode(String phone)
    {
        phone = normalize(phone);
        var existing = user.getUserByPhone(phone);
        if (existing != null)
        {
            return existing;
        }

        var username = generateGuestUsername(phone);
        var newUser = new UserEntity(
                null,
                username,
                phone,
                encodePassword(generateTemporaryPassword(phone)),
                DEFAULT_AUTHORITY
        );
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
        username = normalize(username);
        if (username == null)
        {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty())
        {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (user.getUserByName(username) != null) throw new IllegalArgumentException("用户名重复");
        user.insertUser(
                new UserEntity(null, username, null, encodePassword(password), DEFAULT_AUTHORITY)
        );
    }

    public void updateUsername(String t, String newUsername)
    {
        var u = getUserByToken(t);
        String oldUsername = u.getUsername();
        newUsername = normalize(newUsername);
        if (newUsername == null)
        {
            throw new IllegalArgumentException("新用户名不能为空");
        }
        if (user.getUserByName(newUsername) != null) throw new IllegalArgumentException("用户名重复");
        u.setUsername(newUsername);
        user.updateUsername(u);
        token.forceLogout(oldUsername);
    }


    public void updatePassword(String t, String oldPassword, String newPassword)
    {
        var u = getUserByToken(t);
        if (newPassword == null || newPassword.trim().isEmpty())
        {
            throw new IllegalArgumentException("新密码不能为空");
        }

        if (isPasswordEqual(oldPassword,u.getPassword()))
        {
            u.setPassword(encodePassword(newPassword));
            user.updatePassword(u);
            token.forceLogout(u.getUsername());
        }
        else throw new IllegalArgumentException("密码错误");
    }

    public UserEntity getUserByPhone(String phone)
    {
        return user.getUserByPhone(normalize(phone));
    }

    public UserProfileEntity getUserProfileByToken(String t)
    {
        var u = getUserByToken(t);
        return new UserProfileEntity(
                u.getId(),
                u.getUsername(),
                u.getPhone(),
                u.getAuthority(),
                List.copyOf(getAuthorityCodes(u.getAuthority())),
                hasAdminAuthority(u.getAuthority())
        );
    }

    public boolean hasAdminAuthority(String authority)
    {
        return hasAuthority(authority, AuthorityCode.ADMIN_ACCESS);
    }

    public boolean canReadBlogPublic(String authority)
    {
        return hasAuthority(authority, AuthorityCode.BLOG_READ_PUBLIC)
                || hasAuthority(authority, AuthorityCode.BLOG_READ_FRIENDS)
                || hasAuthority(authority, AuthorityCode.BLOG_READ_PRIVATE);
    }

    public boolean canReadBlogFriends(String authority)
    {
        return hasAuthority(authority, AuthorityCode.BLOG_READ_FRIENDS)
                || hasAuthority(authority, AuthorityCode.BLOG_READ_PRIVATE);
    }

    public boolean canReadBlogPrivate(String authority)
    {
        return hasAuthority(authority, AuthorityCode.BLOG_READ_PRIVATE);
    }

    public boolean hasPermission(String authority, String permission)
    {
        if (permission == null || permission.trim().isEmpty())
        {
            return false;
        }
        if (hasAdminAuthority(authority))
        {
            return true;
        }
        return hasAuthority(authority, permission);
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

    private String normalize(String value)
    {
        if (value == null)
        {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean hasAuthority(String authority, String permission)
    {
        return getAuthorityCodes(authority).contains(permission.trim().toLowerCase());
    }

    private Set<String> getAuthorityCodes(String authority)
    {
        Set<String> codes = new LinkedHashSet<>();
        if (authority == null || authority.trim().isEmpty())
        {
            return codes;
        }

        var list = JsonTool.readJson(authority, new TypeReference<List<String>>() {});
        if (list != null && !list.isEmpty())
        {
            for (String item : list)
            {
                if (item != null && !item.trim().isEmpty())
                {
                    codes.add(item.trim().toLowerCase());
                }
            }
            return codes;
        }

        String normalized = authority.toLowerCase();
        for (Authority item : Authority.values())
        {
            if (item == Authority.NO)
            {
                continue;
            }
            if (normalized.contains("\"" + item.code() + "\"")
                    || normalized.contains("'" + item.code() + "'")
                    || normalized.contains(item.code()))
            {
                codes.add(item.code());
            }
        }
        return codes;
    }
}
