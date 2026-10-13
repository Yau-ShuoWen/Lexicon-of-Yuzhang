package com.shuowen.yuzong.config;

import com.shuowen.yuzong.util.err.IllegalStringException;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.err.APIException;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.HTTPCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse<Void>> handleAPIException(APIException exception)
    {
        return failure(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<Void>> handleJsonError(HttpMessageNotReadableException exception)
    {
        Throwable root = exception.getMostSpecificCause();
        if (root instanceof IllegalStringException illegalString)
        {
            return failure(HTTPCode.InvalidScTcText, illegalString.getMessage());
        }
        if (root instanceof InvalidPinyinException invalidPinyin)
        {
            return failure(HTTPCode.InvalidPinyin, invalidPinyin.getMessage());
        }
        return failure(HTTPCode.BadRequest, "请求格式错误");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException exception)
    {
        Throwable cause = exception.getCause();
        if (cause instanceof IllegalStringException illegalString)
        {
            return failure(HTTPCode.InvalidScTcText, illegalString.getMessage());
        }
        if (cause instanceof InvalidPinyinException invalidPinyin)
        {
            return failure(HTTPCode.InvalidPinyin, invalidPinyin.getMessage());
        }

        String expectedType = exception.getRequiredType() == null
                ? "指定类型"
                : exception.getRequiredType().getSimpleName();
        String message = String.format("参数“%s”格式错误，期望 %s，收到值：%s",
                exception.getName(), expectedType, exception.getValue());
        return failure(HTTPCode.BadRequest, message);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MissingRequestHeaderException.class})
    public ResponseEntity<APIResponse<Void>> handleMissingRequestValue(Exception exception)
    {
        return failure(HTTPCode.BadRequest, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Void>> handleValidation(MethodArgumentNotValidException exception)
    {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("请求参数校验失败");
        return failure(HTTPCode.BadRequest, message);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<APIResponse<Void>> handleBinding(BindException exception)
    {
        String message = exception.getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("请求参数绑定失败");
        return failure(HTTPCode.BadRequest, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Void>> handleBadRequest(IllegalArgumentException exception)
    {
        return failure(HTTPCode.BadRequest, exception.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<APIResponse<Void>> handleNotFound(NoSuchElementException exception)
    {
        return failure(HTTPCode.NotFound, exception.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<APIResponse<Void>> handleForbidden(SecurityException exception)
    {
        return failure(HTTPCode.NoPermission, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleUnexpected(Exception exception)
    {
        exception.printStackTrace();
        return failure(HTTPCode.InternalError, "服务暂时不可用，请稍后重试");
    }

    private ResponseEntity<APIResponse<Void>> failure(HTTPCode code, String message)
    {
        return ResponseEntity.status(code.getHttpStatus())
                .body(APIResponse.failure(code, message));
    }
}
