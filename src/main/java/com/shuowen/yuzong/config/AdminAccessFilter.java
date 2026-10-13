package com.shuowen.yuzong.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.user.data.model.UserEntity;
import com.shuowen.yuzong.user.service.UserService;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.HTTPCode;
import com.shuowen.yuzong.util.err.APIException;
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
            "/api/tool/",
            "/api/pinyin/audio",
            "/api/audio",
            "/api/image",
            "/api/study/edit/",
            "/api/study/loading-text/manage",
            "/api/study/curriculum/manage/",
            "/api/user/update-username",
            "/api/admin/",
            "/upload",
            "/oss"
    );

    private final UserService userService;
    private final ObjectMapper objectMapper;

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
            writeNotLogin(response);
            return;
        }

        final var user = getUser(token, response);
        if (user == null) return;

        if (!userService.hasAdminAuthority(user.getAuthority()))
        {
            writeFailure(response, HTTPCode.NoPermission, "管理员权限不足");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private UserEntity getUser(String token, HttpServletResponse response) throws IOException
    {
        try
        {
            return userService.getUserByToken(token);
        }
        catch (APIException exception)
        {
            writeFailure(response, exception.getCode(), exception.getMessage());
            return null;
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            writeFailure(response, HTTPCode.InternalError, "服务暂时不可用，请稍后重试");
            return null;
        }
    }

    private void writeNotLogin(HttpServletResponse response) throws IOException
    {
        writeFailure(response, HTTPCode.NotLogin, "请先登录");
    }

    private void writeFailure(HttpServletResponse response, HTTPCode code, String message) throws IOException
    {
        response.setStatus(code.getHttpStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(
                response.getWriter(),
                APIResponse.failure(code, message)
        );
    }
}
