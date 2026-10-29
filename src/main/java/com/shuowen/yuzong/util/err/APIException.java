package com.shuowen.yuzong.util.err;

import com.shuowen.yuzong.util.tuple.HTTPCode;
import lombok.Getter;

@Getter
public class APIException extends RuntimeException
{
    private final HTTPCode code;

    public APIException(HTTPCode code, String message)
    {
        super(message);
        this.code = code;
    }
}
