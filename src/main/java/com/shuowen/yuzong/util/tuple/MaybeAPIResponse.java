package com.shuowen.yuzong.util.tuple;

/**
 * 将后端内部的 Maybe 转换成普通 APIResponse。
 */
public final class MaybeAPIResponse
{
    private MaybeAPIResponse()
    {
    }

    public static <T> APIResponse<T> of(Maybe<T> maybe, String emptyMessage)
    {
        if (maybe == null || maybe.isEmpty())
        {
            return APIResponse.failure(HTTPCode.NotFound, emptyMessage);
        }
        return APIResponse.success(maybe.getValue());
    }
}
