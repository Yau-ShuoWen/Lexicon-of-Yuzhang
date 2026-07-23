package com.shuowen.yuzong.Linguistics.Scheme;

import com.shuowen.yuzong.util.text.FLText;
import com.shuowen.yuzong.util.tuple.Pair;

import java.util.LinkedHashMap;

import static com.shuowen.yuzong.util.ext.other.NullTool.checkNotNull;

public class FLCode
{
    private final LinkedHashMap<String, Pair<Integer, String>> data = new LinkedHashMap<>();

    public FLCode(String config)
    {
        checkNotNull(config);

        try
        {
            for (var i : config.split(","))
            {
                String[] split = i.split(":");
                data.put(split[0], Pair.of(Integer.parseInt(split[1]), null));
            }
        } catch (Exception e)
        {
            throw new IllegalArgumentException("""
                    配置格式错误，正确格式：
                    键1:限制长度1,键2:限制长度2...
                    """);
        }
    }

    public void set(String key, String value)
    {
        checkNotNull(key, value);

        if (data.containsKey(key))
        {
            var len = data.get(key).getLeft();
            data.get(key).setRight(FLText.of(value, len));
        }
        else throw new IllegalArgumentException("如果要提前使用字段，请先声明");
    }

    public void setMul(String... kv)
    {
        checkNotNull(kv);
        try
        {
            for (int i = 0; i < kv.length; i += 2)
            {
                set(kv[i], kv[i + 1]);
            }
        } catch (Exception e)
        {
            throw new IllegalArgumentException("""
                    配置格式错误，正确格式：
                    键1,值1,键2,值2...
                    """);
        }
    }


    public String toString()
    {
        String s = "";
        for (var i : data.values())
        {
            checkNotNull(i.getRight(), "还有没有设置完的部分");
            s += i.getRight();
        }
        return s;
    }
}
