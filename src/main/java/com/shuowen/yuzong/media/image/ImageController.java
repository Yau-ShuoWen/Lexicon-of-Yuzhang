package com.shuowen.yuzong.media.image;

import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.HTTPCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController
{
    private final ImageService imageService;

    @PostMapping("/upload")
    public APIResponse<ImageVO> upload(@RequestParam("file") MultipartFile file) throws IOException
    {
        return APIResponse.success(imageService.upload(file));
    }

    @GetMapping("/list")
    public APIResponse<List<ImageVO>> list()
    {
        return APIResponse.success(imageService.list());
    }

    @GetMapping("/{id}")
    public APIResponse<ImageVO> get(@PathVariable Long id)
    {
        return APIResponse.success(imageService.get(id));
    }

    @DeleteMapping("/{id}")
    public APIResponse<Void> delete(@PathVariable Long id)
    {
        imageService.delete(id);
        return APIResponse.success();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Void>> invalid(IllegalArgumentException exception)
    {
        boolean notFound = "图片不存在".equals(exception.getMessage());
        HttpStatus status = notFound ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        HTTPCode code = notFound ? HTTPCode.NotFound : HTTPCode.BadRequest;
        return ResponseEntity.status(status).body(APIResponse.failure(code, exception.getMessage()));
    }
}
