package com.shuowen.yuzong.Tool;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * 正字法规则
 */
@Data
public class OrthoCharset
{
    Set<String> escape;
    Map<String, String> handle;

    public OrthoCharset(){}
}
