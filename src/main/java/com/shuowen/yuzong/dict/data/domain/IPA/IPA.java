package com.shuowen.yuzong.dict.data.domain.IPA;

import java.util.Objects;

/**
 * 已完成转写、可以插入富文本的国际音标。
 */
public record IPA(String value)
{
    public IPA
    {
        Objects.requireNonNull(value, "IPA内容不能为空");
    }

    /**
     * 富文本使用前导减号识别国际音标，具体字体由前端按字符处理。
     */
    @Override
    public String toString()
    {
        return "[-" + value + "]";
    }
}
