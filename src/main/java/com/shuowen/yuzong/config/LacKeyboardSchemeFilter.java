package com.shuowen.yuzong.config;

import com.shuowen.yuzong.linguistics.pinyinv2.lac.LACPinyin;
import com.shuowen.yuzong.linguistics.pinyinv2.lac.LacKeyboardSchemeContext;
import com.shuowen.yuzong.user.service.UserSettingService;
import com.shuowen.yuzong.util.err.APIException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@RequiredArgsConstructor
public class LacKeyboardSchemeFilter extends OncePerRequestFilter
{
    private final UserSettingService settings;
    private static final List<String> PINYIN_PATHS = List.of(
            "/api/pinyin", "/api/proofread", "/api/edit/", "/api/ref", "/api/study", "/api/search/");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
    {
        String path = request.getRequestURI();
        return path == null || PINYIN_PATHS.stream().noneMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException
    {
        String token = request.getHeader("X-Auth-Token");
        if (token == null || token.isBlank()) token = request.getParameter("t");
        var scheme = LACPinyin.KeyboardScheme.STANDARD;
        if (token != null && !token.isBlank())
        {
            try
            {
                if (settings.usesLegacyPinyin(token)) scheme = LACPinyin.KeyboardScheme.LEGACY;
            }
            catch (APIException ignored)
            {
                // 公共接口携带过期令牌时仍按正式方案处理；受保护接口自行验证令牌。
            }
        }
        LacKeyboardSchemeContext.set(scheme);
        try
        {
            chain.doFilter(request, response);
        }
        finally
        {
            LacKeyboardSchemeContext.clear();
        }
    }
}
