package com.shuowen.yuzong.Tool.dataStructure;

public class ErrorInfo
{
    private Maybe<String> info;

    private ErrorInfo(String info)
    {
        this.info = Maybe.exist(info);
    }

    private ErrorInfo()
    {
        this.info = Maybe.nothing();
    }

    public static ErrorInfo of(String info)
    {
        return new ErrorInfo(info);
    }

    public static ErrorInfo of()
    {
        return new ErrorInfo();
    }

    public String info()
    {
        return info.getValueOrDefault("无");
    }
}
