package com.shuowen.yuzong.service.impl.Account;

import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.data.domain.Account.User;
import com.shuowen.yuzong.data.mapper.Account.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public boolean checkIdentity(String username, String password)
    {
        var u = User.tryOf(username, i -> user.getUserByName(i));
        return u.isValid() && u.getValue().isPasswordEqual(password);
    }

    /**
     * 通过令牌获得用户数据
     */
    public Maybe<User> getUserByToken(String t)
    {
        return User.tryOf(t, i -> token.getUsernameByToken(i), i -> user.getUserByName(i));
    }

    public boolean isUsernameRepeated(String username)
    {
        return user.getUserByName(username) != null;
    }


    public void createUser(String username, String password)
    {
        User u = new User();
        if (!isUsernameRepeated(username))
        {
            u.setUsername(username);
            u.setPassword(password);
            user.insertUser(u.transfer());
        }
        else throw new IllegalArgumentException("用户名重复。Username is repetitive.");
    }


    public void updateUsername(String t, String newUsername)
    {
        var maybeUser = getUserByToken(t);
        if (!maybeUser.isValid()) throw new IllegalArgumentException("登陆状态无效。Invalid login status.");

        var u = maybeUser.getValue();
        if (!isUsernameRepeated(newUsername))
        {
            u.setUsername(newUsername);
            user.updateUsername(u.transfer());
            token.removeToken(t);
        }
        else throw new IllegalArgumentException("用户名重复。Username is repetitive.");
    }


    public void updatePassword(String t, String oldPassword, String newPassword)
    {
        var maybeUser = getUserByToken(t);
        if (!maybeUser.isValid()) throw new IllegalArgumentException("登陆状态无效。Invalid login status.");

        var u = maybeUser.getValue();
        if (u.isPasswordEqual(oldPassword))
        {
            u.setPassword(newPassword);
            user.updatePassword(u.transfer());
        }
        else throw new IllegalArgumentException("密码错误。Password incorrect.");
    }
}
