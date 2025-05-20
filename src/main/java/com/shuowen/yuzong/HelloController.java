package com.shuowen.yuzong;

import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;



@RestController
@RequestMapping ("/api")
public class HelloController
{
    @GetMapping ("/hello")
    public Map<String, String> sayHello()
    {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from Backend! shuowen!");
        return response;
    }

    // 🔹 使用一个对象接收 JSON，避免解析错误
    @PostMapping ("/process")
    public Map<String, String> processString(@RequestBody InputRequest request)
    {
        Map<String, String> response = new HashMap<>();

        response.put("message", NamPinyin.parseAndReplace(request.getInput()));
        return response;
    }
}

// 🔹 定义一个数据传输对象 (DTO)
class InputRequest
{
    private String input;

    public String getInput()
    {
        return input;
    }

    public void setInput(String input)
    {
        this.input = input;
    }
}