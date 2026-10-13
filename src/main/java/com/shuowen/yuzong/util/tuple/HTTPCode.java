package com.shuowen.yuzong.util.tuple;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum HTTPCode
{
    OK(200, 200),
    BadRequest(400, 400),
    NotLogin(401, 401),
    NoPermission(403, 403),
    NotFound(404, 404),
    Conflict(409, 409),
    InternalError(500, 500),

    InvalidPinyin(49001, 400),
    InvalidScTcText(49002, 400);

    @JsonValue
    @Getter
    private final int code;

    @Getter
    private final int httpStatus;
}
