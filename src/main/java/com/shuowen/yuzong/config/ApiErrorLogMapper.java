package com.shuowen.yuzong.config;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiErrorLogMapper
{
    @Insert("""
            INSERT INTO NC.api_error_log (request_path, error_source, exception_type, error_message, stack_trace)
            VALUES (#{path}, #{source}, #{type}, #{message}, #{stackTrace})
            """)
    void insert(@Param("path") String path,
                @Param("source") String source,
                @Param("type") String type,
                @Param("message") String message,
                @Param("stackTrace") String stackTrace);
}
