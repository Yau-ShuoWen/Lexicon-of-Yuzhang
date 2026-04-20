package com.shuowen.yuzong.config;

import com.shuowen.yuzong.Tool.dataStructure.error.IllegalStringException;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler (HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleJsonError(HttpMessageNotReadableException ex)
    {

        Throwable root = ex.getMostSpecificCause();

        if (root instanceof IllegalStringException ise)
        {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", "SC_TC_LENGTH_MISMATCH",
                    "message", ise.getMessage()
            ));
        }

        if (root instanceof InvalidPinyinException ise)
        {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", "SC_TC_LENGTH_MISMATCH",
                    "message", ise.getMessage()
            ));
        }

        // 其他 JSON 錯誤
        return ResponseEntity.badRequest().body(Map.of(
                "code", "INVALID_JSON",
                "message", "請求格式錯誤"
        ));
    }

    @ExceptionHandler (MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex)
    {
        Throwable rootCause = ex.getCause();  // 很重要！你的 IllegalStringException 通常在这里

        // 如果是我们的自定义异常，优先用它的消息
        if (rootCause instanceof IllegalStringException ise)
        {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", "SC_TC_LENGTH_MISMATCH",   // 或换成更合适的 code，如 "INVALID_UCHAR"
                    "message", ise.getMessage(),
                    "parameter", ex.getName(),         // 可选：告诉前端是哪个参数错了
                    "value", ex.getValue()             // 可选：用户传入的错误值
            ));
        }

        if (rootCause instanceof InvalidPinyinException ipe)
        {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", "SC_TC_LENGTH_MISMATCH",
                    "message", ipe.getMessage()
            ));
        }

        // 通用类型转换失败（例如 query=书1 转 UChar 失败）
        String message = String.format("参数 '%s' 类型转换失败，期望 %s，收到值: %s",
                ex.getName(),
                ex.getRequiredType() == null ? "" : ex.getRequiredType().getSimpleName(),
                ex.getValue());

        return ResponseEntity.badRequest().body(Map.of(
                "code", "TYPE_MISMATCH",
                "message", message,
                "parameter", ex.getName()
        ));
    }
}