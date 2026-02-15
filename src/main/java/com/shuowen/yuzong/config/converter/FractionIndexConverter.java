package com.shuowen.yuzong.config.converter;

import org.springframework.core.convert.converter.Converter;
import com.shuowen.yuzong.Tool.FractionIndex;
import com.shuowen.yuzong.Tool.Obfuscation;
import org.springframework.stereotype.Component;

@Component
public class FractionIndexConverter implements Converter<String, FractionIndex>
{
    @Override
    public FractionIndex convert(String source)
    {
        return FractionIndex.of(Obfuscation.decode(source));
    }
}
