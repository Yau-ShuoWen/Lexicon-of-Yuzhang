package com.shuowen.yuzong.util.err;

public class InvalidPinyinException extends RuntimeException
{
    public InvalidPinyinException(String msg)
    {
        super(msg);
    }

    public InvalidPinyinException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
