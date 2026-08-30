package com.shuowen.yuzong.util.version;

import java.util.List;

public record TextDiffResponse(
        String source,
        String target,
        List<TextDiffDelta> changes
)
{
}
