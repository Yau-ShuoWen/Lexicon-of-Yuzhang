package com.shuowen.yuzong.util.version;

/**
 * 双栏文本比较请求。
 * source 对应左侧文本，target 对应右侧文本。
 */
public record TextDiffRequest(String source, String target)
{
}
