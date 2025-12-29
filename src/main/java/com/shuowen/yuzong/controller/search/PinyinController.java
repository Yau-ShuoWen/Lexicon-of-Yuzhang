package com.shuowen.yuzong.controller.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinPreviewer;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/pinyin/")
public class PinyinController
{
    @GetMapping (value = "{dialect}/style-init")
    public PinyinStyle pinyin(
            @PathVariable String dialect,
            @RequestParam Integer SchemeParam
    )
    {
        return Dialect.of(dialect).createStyle(PinyinParam.of(Scheme.of(SchemeParam)));
    }

    /**
     * 传入拼音配置，预览效果
     */
    @PostMapping ("{dialect}/preview")
    public APIResponse<String> preview(
            @PathVariable String dialect,
            @RequestBody Map<String, Object> styleParam)
    {
        Dialect d = Dialect.of(dialect);
        return APIResponse.success(PinyinPreviewer.getPreview(
                new ObjectMapper().convertValue(styleParam, d.getStyleClass()), d));
    }

    @GetMapping ("{dialect}/normalize")
    public Triple<Integer, String, String> normalizeCheck(
            @PathVariable String dialect,
            @RequestParam String pinyin)
    {
        return PinyinChecker.check(pinyin, Dialect.of(dialect));
    }
}
