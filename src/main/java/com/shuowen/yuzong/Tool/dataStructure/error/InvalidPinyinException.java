package com.shuowen.yuzong.Tool.dataStructure.error;

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
