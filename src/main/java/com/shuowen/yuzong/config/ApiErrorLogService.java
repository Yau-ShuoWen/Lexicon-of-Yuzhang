package com.shuowen.yuzong.config;

import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class ApiErrorLogService
{
    private final ApiErrorLogMapper mapper;

    public ApiErrorLogService(ApiErrorLogMapper mapper)
    {
        this.mapper = mapper;
    }

    public void record(String path, String source, Exception exception)
    {
        try
        {
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            mapper.insert(limit(path, 500), source, exception.getClass().getName(),
                    limit(exception.getMessage(), 2000), limit(writer.toString(), 12000));
        }
        catch (Exception loggingFailure)
        {
            // 记录失败不能覆盖原始接口错误，例如数据库本身暂时不可用。
            System.err.println("API 错误写入数据库失败: " + loggingFailure.getMessage());
            exception.printStackTrace();
        }
    }

    private String limit(String value, int maxLength)
    {
        if (value == null) return null;
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
