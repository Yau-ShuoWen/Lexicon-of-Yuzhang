package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.service.impl.KeyValueService;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinDetail;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/pinyin/")
public class PinyinController
{
    @Autowired
    KeyValueService kv;

    @GetMapping ("{dialect}/style-init")
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
        try
        {
            Dialect d = Dialect.of(dialect);
            String text = kv.get("pinyin-style-display-text:"+d.toString());
            var style = d.createStyle(styleParam);

            return APIResponse.success(RichTextUtil.format(text, style, d));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("{dialect}/normalize")
    public Triple<Integer, String, String> normalizeCheck(
            @PathVariable String dialect,
            @RequestParam String pinyin)
    {
        return PinyinChecker.suggestively(pinyin, Dialect.of(dialect));
    }

    @GetMapping ("{dialect}/table")
    public List<List<PinyinDetail>> getTable(@PathVariable String dialect)
    {
        return Dialect.of(dialect).getTable();
    }

    @GetMapping ("{dialect}/get-tone-preview")
    public List<PinyinDetail> getTone(
            @PathVariable String dialect,
            @RequestParam String last)
    {
        return PinyinFormatter.getTonePreview(Dialect.of(dialect), last);
    }
}
