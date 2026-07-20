package com.shuowen.yuzong.service.impl.Account;

import com.shuowen.yuzong.data.model.Account.UserEntity;
import com.shuowen.yuzong.data.mapper.Account.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.shuowen.yuzong.authority.PasswordUtil.encodePassword;
import static com.shuowen.yuzong.authority.PasswordUtil.isPasswordEqual;
import static com.shuowen.yuzong.util.ext.other.NullTool.assertNotNull;

@Service
@Transactional (rollbackFor = {Exception.class})
public class UserService
{
    @Autowired
    private UserMapper user;

    @Autowired
    private TokenService token;

    /**
     * 检查用户名、密码是否对应，用于登陆
     */
    public void checkIdentity(String username, String password)
    {
        var u = assertNotNull(user.getUserByName(username), new NoSuchElementException("用户不存在"));
        if (!isPasswordEqual(password, u.getPassword()))
            throw new IllegalArgumentException("用户名或者密码错误");
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
                new UserEntity(null, username, encodePassword(password), "[]")
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
}
