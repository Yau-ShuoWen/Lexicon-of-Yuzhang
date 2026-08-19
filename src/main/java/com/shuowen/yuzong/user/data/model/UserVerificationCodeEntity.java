package com.shuowen.yuzong.user.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserVerificationCodeEntity
{
    private Long id;
    private String phone;
    private String code;
    private String purpose;
    private Boolean used;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime usedAt;
}
