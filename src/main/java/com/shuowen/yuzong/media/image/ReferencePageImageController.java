package com.shuowen.yuzong.media.image;

import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.util.ext.sort.FractionIndex;
import com.shuowen.yuzong.util.tuple.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/ref/page-image")
@RequiredArgsConstructor
public class ReferencePageImageController
{
    private final ReferencePageImageService service;

    @GetMapping("/{dictionary}")
    public APIResponse<ImageVO> get(@PathVariable DictCode dictionary, @RequestParam FractionIndex sort)
    {
        return APIResponse.success(service.get(dictionary, sort));
    }

    @PostMapping("/{dictionary}")
    public APIResponse<ImageVO> upload(
            @PathVariable DictCode dictionary,
            @RequestParam FractionIndex sort,
            @RequestParam("file") MultipartFile file) throws IOException
    {
        return APIResponse.success(service.upload(dictionary, sort, file));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Void>> invalid(IllegalArgumentException exception)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.failure(exception.getMessage()));
    }
}
