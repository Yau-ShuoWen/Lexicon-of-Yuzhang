package com.shuowen.yuzong.util.tuple;

import lombok.Data;

@Data
public class APIResponse<T>
{
    private boolean success;
    private int code;
    private String message;
    private T data;

    private APIResponse(boolean success, int code, String message, T data)
    {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 返回值的静态方法
     */
    public static <T> APIResponse<T> success(T data)
    {
        return new APIResponse<>(true, HTTPCode.OK.getCode(), "success", data);
    }

    /**
     * 无参数的静态方法
     */
    public static APIResponse<Void> success()
    {
        return new APIResponse<>(true, HTTPCode.OK.getCode(), "success", null);
    }

    /**
     * 失败的静态方法
     */
    @Deprecated
    public static <T> APIResponse<T> failure(String msg)
    {
        return failure(HTTPCode.InternalError, msg);
    }

    /**
     * 带明确状态码的失败响应。
     * <p>
     * 旧接口仍可继续调用 failure(String)，迁移后的接口应使用此方法。
     * code 是稳定的响应码；HTTP 状态由 Web 层根据 HTTPCode.httpStatus 设置。
     */
    public static <T> APIResponse<T> failure(int code, String msg)
    {
        return new APIResponse<>(false, code, normalizeMessage(msg), null);
    }

    public static <T> APIResponse<T> failure(HTTPCode code, String msg)
    {
        return failure(code.getCode(), msg);
    }

    private static String normalizeMessage(String message)
    {
        return message == null || message.isBlank() ? "请求失败" : message;
    }
}
