package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.OrthoCharset;
import com.shuowen.yuzong.Tool.ProofreadTool;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Trio;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.IPA.IPAData;
import com.shuowen.yuzong.data.domain.IPA.PinyinOption;
import com.shuowen.yuzong.data.domain.Reference.DictCode;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/proofread")
public class ProofreadController
{
    @PostMapping ("sc-tc-translate/{d}")
    public APIResponse<Map<String, UString>> translate(
            @PathVariable Dialect d,
            @RequestBody Trio<String> text
    )
    {
        try
        {
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

    @GetMapping ("/check/{l}/{d}")
    public Pair<Boolean, UString> check(@PathVariable Dialect d, @PathVariable Language l,
                                        @RequestParam UString text,
                                        @RequestParam (required = false) DictCode dict)
    {
        var str = RichTextUtil.format(text, new IPAData(
                l, d, PinyinOption.defaultOf()), true, Maybe.uncertain(dict));
        return Pair.of(RichTextUtil.checkWarning(str), str);
    }
}
