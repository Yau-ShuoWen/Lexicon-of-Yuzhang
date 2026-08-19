package com.shuowen.yuzong.config;

import com.shuowen.yuzong.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class AdminAccessFilter extends OncePerRequestFilter
{
    private static final List<String> PROTECTED_PREFIXES = List.of(
            "/api/edit/",
            "/api/ref/",
            "/api/pinyin/audio",
            "/api/audio",
            "/api/user/update-username",
            "/upload",
            "/oss"
    );

    private final UserService userService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
    {
        String path = request.getRequestURI();
        if (path == null)
        {
            return true;
        }
        if (path.startsWith("/api/user"))
        {
            return true;
        }
        return PROTECTED_PREFIXES.stream().noneMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        String token = request.getParameter("t");
        if (token == null || token.trim().isEmpty())
        {
            token = request.getHeader("X-Auth-Token");
        }

        if (token == null || token.trim().isEmpty())
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "管理员权限不足");
            return;
        }

        var user = userService.getUserByToken(token);
        if (!userService.hasAdminAuthority(user.getAuthority()))
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "管理员权限不足");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
