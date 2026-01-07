package com.shuowen.yuzong.Tool.dataStructure.error;

public class ExceptionTool
{
    /**
     * 对于意料之内的报错信息，就不输出了
     */
    public static void printIfUnexpectedly(Exception e, String... messages)
    {
        for (String msg : messages)
            if (!e.getMessage().contains(msg)) e.printStackTrace();
    }

    //TODO：对于意料之内的报错信息，就不记录日志了
}
