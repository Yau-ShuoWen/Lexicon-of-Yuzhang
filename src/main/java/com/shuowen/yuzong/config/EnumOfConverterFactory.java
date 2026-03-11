package com.shuowen.yuzong.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class EnumOfConverterFactory implements ConverterFactory<String, Enum<?>>
{

    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType)
    {
        return new EnumOfConverter<>(targetType);
    }

    private static class EnumOfConverter<T extends Enum> implements Converter<String, T>
    {

        private final Class<T> enumType;

        EnumOfConverter(Class<T> enumType)
        {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source)
        {

            source = source.trim();

            try  // 1 先尝试 of(String)
            {
                Method m = enumType.getMethod("of", String.class);
                return (T) m.invoke(null, source);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored)
            {
            }

            try// 2 再尝试 of(int)
            {
                Method m = enumType.getMethod("of", int.class);
                return (T) m.invoke(null, Integer.parseInt(source));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored)
            {
            }

            // 3 fallback 到 valueOf
            return (T) Enum.valueOf(enumType, source);
        }
    }
}