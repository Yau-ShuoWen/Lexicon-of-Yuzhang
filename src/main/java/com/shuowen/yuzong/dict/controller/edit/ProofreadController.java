package com.shuowen.yuzong.dict.controller.edit;

import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.dict.data.domain.Pinyin.PinyinConfig;
import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.util.text.*;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.Pair;
import com.shuowen.yuzong.util.tuple.Trio;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/proofread")
public class ProofreadController
{
    @PostMapping ("sc-tc-translate/{d}")
    public APIResponse<ScTcText> translate(
            @PathVariable Dialect d,
            @RequestBody Trio<UString> text
    )
    {
        try
        {
            return APIResponse.success(ProofreadTool.retainContextTranslate(
                    text.getLeft(), text.getMiddle(), text.getRight(),
                    OrthoCharset.of(d))
            );
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/check/{l}/{d}")
    public Pair<Boolean, UString> check(@PathVariable Dialect d, @PathVariable Language l,
                                        @RequestParam (required = false) DictCode dict,
                                        @RequestParam UString text
    )
    {
        var str = RichTextUtil.format(text, new PinyinConfig(l, d), true, Maybe.uncertain(dict), false);
        return Pair.of(RichTextUtil.checkWarning(str), str);
    }

    @PostMapping ("/translate")
    public APIResponse<String> translate(@RequestParam String from, @RequestParam String to, @RequestBody String text)
    {
        try
        {
            return APIResponse.success(ProofreadTool.translate(text, from, to));
        } catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }
}
