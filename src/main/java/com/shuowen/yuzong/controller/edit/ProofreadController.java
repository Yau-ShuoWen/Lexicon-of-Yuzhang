package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Linguistics.Mandarin.TcSc;
import com.shuowen.yuzong.Tool.ProofreadTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Trio;
import com.shuowen.yuzong.controller.APIResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping ("/api/transfer")
public class ProofreadController
{

    @PostMapping ("{dialect}/sc-tc-translate")
    public APIResponse<Map<String, UString>> translate(
            @PathVariable String dialect,
            @RequestBody Trio<String> text
    )
    {
        try
        {   //var d= Dialect.of(dialect); 是为了之后查询Set.of什么用的
            var UText = text.map(UString::of);
            return APIResponse.success(
                    ProofreadTool.retainContextTranslate(UText.getLeft(), UText.getMiddle(), UText.getRight(), Set.of("箇", "嗰"))
            );
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    /**
     * 使用hanlp直接繁体转简体
     *
     * @apiNote 没有「简转繁」是因为简转繁不精确，编辑者必须把关繁体版本，才能放心交给程序简化
     */
    @RequestMapping ("/tc")
    public Map<String, String> t2s(@RequestParam String tc)
    {
        return Map.of("sc", TcSc.t2s(tc));
    }

    /**
     * 半角符号转全角
     */
    @RequestMapping ("/full-width")
    public Map<String, String> fullWidth(@RequestParam String s)
    {
        return Map.of("text", s.
                replace(",", "，").
                replace(".", "。").
                replace("?", "？").
                replace("!", "！").
                replace("...", "……").
                replace(":", "：").
                replace(";", "；").
                replace("(", "（").
                replace(")", "）").
                replace("·", " · ")
        );
    }

    /**
     *
     */
    @RequestMapping ("/half-width")
    public Map<String, String> halfWidth(@RequestParam String s)
    {
        return Map.of("text", s.
                replace("【", "[").
                replace("】", "]")
        );
    }
}
