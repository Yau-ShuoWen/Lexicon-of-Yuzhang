package com.shuowen.yuzong.user.service;

import com.shuowen.yuzong.user.data.mapper.UserTokenMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DbTokenStoreTest
{
    @Mock
    private UserTokenMapper mapper;

    @InjectMocks
    private DbTokenStore tokenStore;

    @Test
    void saveShouldKeepExistingSessionsForSameUser()
    {
        tokenStore.save("token-a", "alice", 3600);

        verify(mapper, never()).deleteByUsername("alice");
        verify(mapper).insert(any());
    }
}
