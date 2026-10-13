package com.shuowen.yuzong.util.tuple;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NearbyResponse<T>
{
    private T left;
    private T right;

    public static <T> NearbyResponse<T> from(Twin<Maybe<T>> nearby)
    {
        return new NearbyResponse<>(valueOf(nearby.getLeft()), valueOf(nearby.getRight()));
    }

    private static <T> T valueOf(Maybe<T> maybe)
    {
        return maybe != null && maybe.isValid() ? maybe.getValue() : null;
    }
}
