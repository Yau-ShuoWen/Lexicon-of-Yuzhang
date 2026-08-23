package com.shuowen.yuzong.user.service;

import com.shuowen.yuzong.user.data.mapper.UserMapper;
import com.shuowen.yuzong.user.data.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static com.shuowen.yuzong.user.utils.PasswordUtil.encodePassword;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceSessionPolicyTest
{
    @Mock
    private UserMapper userMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    @Test
    void updateUsernameShouldForceLogoutAllSessionsOfOldUsername()
    {
        UserEntity currentUser = new UserEntity(1, "alice", "13800000000", encodePassword("secret"), "[]");
        when(tokenService.getUsernameByToken("token-a")).thenReturn("alice");
        when(userMapper.getUserByName("alice")).thenReturn(currentUser);
        when(userMapper.getUserByName("alice-new")).thenReturn(null);

        userService.updateUsername("token-a", "alice-new");

        verify(userMapper).updateUsername(any(UserEntity.class));
        verify(tokenService).forceLogout("alice");
    }

    @Test
    void updatePasswordShouldForceLogoutAllSessionsOfCurrentUser()
    {
        UserEntity currentUser = new UserEntity(1, "alice", "13800000000", encodePassword("old-pass"), "[]");
        when(tokenService.getUsernameByToken("token-a")).thenReturn("alice");
        when(userMapper.getUserByName("alice")).thenReturn(currentUser);

        userService.updatePassword("token-a", "old-pass", "new-pass");

        verify(userMapper).updatePassword(any(UserEntity.class));
        verify(tokenService).forceLogout("alice");
        verify(tokenService, org.mockito.Mockito.never()).removeToken(eq("token-a"));
    }
}
