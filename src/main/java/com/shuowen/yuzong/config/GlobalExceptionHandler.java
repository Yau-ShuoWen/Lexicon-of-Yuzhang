package com.shuowen.yuzong.config;

import com.shuowen.yuzong.Tool.dataStructure.error.IllegalStringException;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}