package com.shuowen.yuzong.util.tuple;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum HTTPCode
{
    NotLogin(401),
    NoPermission(403),
    OK(200),

    InvalidPinyin(49001),
    InvalidScTcText(49002);

    @JsonValue
    @Getter
    int code;
}
