package com.shuowen.yuzong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {}) // 启用CORS配置
                .csrf(csrf -> csrf.disable()) // 禁用CSRF保护
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll() // 允许所有请求
                );
        return http.build();
    }
}