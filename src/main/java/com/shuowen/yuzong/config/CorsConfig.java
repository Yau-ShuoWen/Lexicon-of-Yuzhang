package com.shuowen.yuzong.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer
{
//    @Override
//    public void addCorsMappings(CorsRegistry registry)
//    {
//        registry.addMapping("/**")
//                .allowedOrigins("*") // 允许所有来源
//                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的方法
//                .allowedHeaders("*") // 允许所有头部
//                .allowCredentials(false)
//                .maxAge(3600);
//    }
}
