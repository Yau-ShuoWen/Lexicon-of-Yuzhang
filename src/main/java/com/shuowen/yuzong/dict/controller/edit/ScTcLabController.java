package com.shuowen.yuzong.dict.controller.edit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.util.map.KV;
import com.shuowen.yuzong.util.tuple.APIResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/edit/sc-tc-lab")
public class ScTcLabController
{
    private static final String KEY = "developer-sc-tc-lab-v1";
    private final ObjectMapper json = new ObjectMapper();

    @GetMapping
    public APIResponse<String> read()
    {
        return APIResponse.success(KV.get(KEY));
    }

    @PostMapping
    @Transactional
    public APIResponse<Void> write(@RequestBody String value) throws Exception
    {
        if (value.length() > 100_000) throw new IllegalArgumentException("测试文本过长");
        JsonNode pair = json.readTree(value);
        if (pair == null || !pair.isObject() || !pair.path("sc").isTextual() || !pair.path("tc").isTextual()
                || pair.size() != 2)
            throw new IllegalArgumentException("只接受包含 sc 和 tc 的 JSON 对象");
        String sc = pair.path("sc").asText();
        String tc = pair.path("tc").asText();
        if (sc.codePointCount(0, sc.length()) != tc.codePointCount(0, tc.length()))
            throw new IllegalArgumentException("简繁字数不一致");
        KV.set(KEY, pair.toString());
        return APIResponse.success();
    }
}
