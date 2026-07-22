package com.shuowen.yuzong.controller.setting;

import com.shuowen.yuzong.controller.APIResponse;
import org.springframework.web.bind.annotation.*;

/**
 * 基礎测试网络连通，可以在测试网络连接的时候使用，给出了示例的网络接口
 */

@RestController
@CrossOrigin (origins = "*")
@RequestMapping ("/api")
public class HelloController
{
    // 无参数：http://localhost:8080/api/hello
    // 有参数：http://localhost:8080/api/hello?name=ysw
    @GetMapping ("/hello")
    public APIResponse<String> hello(@RequestParam (required = false, defaultValue = "") String name)
    {
        return APIResponse.success(name.isEmpty() ? "你好，世界" : "你好，" + name);
    }
}
