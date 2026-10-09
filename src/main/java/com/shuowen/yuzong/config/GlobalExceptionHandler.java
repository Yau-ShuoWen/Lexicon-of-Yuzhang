package com.shuowen.yuzong.config;

import com.shuowen.yuzong.util.err.IllegalStringException;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.HTTPCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler (HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<Void>> handleJsonError(HttpMessageNotReadableException ex)
    {

        Throwable root = ex.getMostSpecificCause();

        if (root instanceof IllegalStringException ise)
        {
            return ResponseEntity.badRequest().body(
                    APIResponse.failure(HTTPCode.InvalidScTcText, ise.getMessage())
            );
        }

        if (root instanceof InvalidPinyinException ise)
        {
            return ResponseEntity.badRequest().body(
                    APIResponse.failure(HTTPCode.InvalidPinyin, ise.getMessage())
            );
        }

        // 其他 JSON 錯誤
        return ResponseEntity.badRequest().body(APIResponse.failure(400, "請求格式錯誤"));
    }

    @ExceptionHandler (MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex)
    {
        Throwable rootCause = ex.getCause();  // 很重要！你的 IllegalStringException 通常在这里

        // 如果是我们的自定义异常，优先用它的消息
        if (rootCause instanceof IllegalStringException ise)
        {
            return ResponseEntity.badRequest().body(
                    APIResponse.failure(HTTPCode.InvalidScTcText, ise.getMessage())
            );
        }

        if (rootCause instanceof InvalidPinyinException ipe)
        {
            return ResponseEntity.badRequest().body(
                    APIResponse.failure(HTTPCode.InvalidPinyin, ipe.getMessage())
            );
        }

        // 通用类型转换失败（例如 query=书1 转 UChar 失败）
        String message = String.format("参数 '%s' 类型转换失败，期望 %s，收到值: %s",
                ex.getName(),
                ex.getRequiredType() == null ? "" : ex.getRequiredType().getSimpleName(),
                ex.getValue());

        return ResponseEntity.badRequest().body(APIResponse.failure(400, message));
    }
}
