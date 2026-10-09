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
        return new APIResponse<>(true, 200, "success", data);
    }

    /**
     * 无参数的静态方法
     */
    public static APIResponse<Void> success()
    {
        return new APIResponse<>(true, 200, "success", null);
    }

    /**
     * 失败的静态方法
     */
    public static <T> APIResponse<T> failure(String msg)
    {
        return failure(404, msg);
    }

    /**
     * 带明确状态码的失败响应。
     * <p>
     * 旧接口仍可继续调用 failure(String)，迁移后的接口应使用此方法，
     * 并让 HTTP 状态与响应体中的 code 保持一致。
     */
    public static <T> APIResponse<T> failure(int code, String msg)
    {
        return new APIResponse<>(false, code, "错误：" + msg, null);
    }
}
