package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.OrthoCharset;
import com.shuowen.yuzong.Tool.ProofreadTool;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Trio;
import com.shuowen.yuzong.controller.APIResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        {
            var d = Dialect.of(dialect);
            var UText = text.map(UString::of); // 转字符串

            return APIResponse.success(ProofreadTool.retainContextTranslate(
                    UText.getLeft(), UText.getMiddle(), UText.getRight(),
                    new OrthoCharset(d))
            );
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }
}
