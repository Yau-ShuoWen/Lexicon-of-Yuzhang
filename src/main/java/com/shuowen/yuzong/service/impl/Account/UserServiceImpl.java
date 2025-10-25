package com.shuowen.yuzong.service.impl.Account;

import com.shuowen.yuzong.dao.domain.Account.User;
import com.shuowen.yuzong.dao.mapper.Account.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl
{
    @Autowired
    private UserMapper m;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean check(String username, String password)
    {
        var a = m.getUser(username);
        if (a == null) return false;
        return (encoder.matches(password, a.getPassword()));
    }

    public boolean checkUsernameRepeated(String username)
    {
        return m.getUser(username) != null;
    }

    public void insertUser(String username, String password)
    {
        m.insertUser(User.of(username, encoder.encode(password)).transfer());
    }
}
