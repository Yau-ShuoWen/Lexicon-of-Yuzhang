package com.shuowen.yuzong.controller;

import lombok.Data;

@Data
public class APIResponse<T>
{
    private boolean success;
    private String message;
    private T data;

    private APIResponse(boolean success, String message, T data)
    {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * 返回值的静态方法
     */
    public static <T> APIResponse<T> success(T data)
    {
        return new APIResponse<>(true, "success", data);
    }

    /**
     * 无参数的静态方法
     */
    public static APIResponse<Void> success()
    {
        return new APIResponse<>(true, "success", null);
    }

    /**
     * 失败的静态方法
     */
    public static <T> APIResponse<T> failure(String msg)
    {
        return new APIResponse<>(false, "后端发生错误：" + msg, null);
    }
}