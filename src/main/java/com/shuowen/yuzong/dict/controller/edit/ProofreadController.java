package com.shuowen.yuzong.dict.controller.edit;

import com.shuowen.yuzong.dict.data.domain.Reference.DictCode;
import com.shuowen.yuzong.dict.pinyin.data.PinyinConfig;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
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
        return APIResponse.success(ProofreadTool.retainContextTranslate(
                text.getLeft(), text.getMiddle(), text.getRight(),
                OrthoCharset.of(d))
        );
    }

    @PostMapping ("/check/{l}/{d}")
    public APIResponse<Pair<Boolean, UString>> check(@PathVariable Dialect d, @PathVariable Language l,
                                        @RequestParam (required = false) DictCode dict,
                                        @RequestParam UString text
    )
    {
        var str = RichTextUtil.format(text, new PinyinConfig(l, d), true, Maybe.uncertain(dict), false);
        return APIResponse.success(Pair.of(RichTextUtil.checkWarning(str), str));
    }

    @PostMapping ("/translate")
    public APIResponse<String> translate(@RequestParam String from, @RequestParam String to, @RequestBody String text)
    {
        return APIResponse.success(ProofreadTool.translate(text, from, to));
    }
}
