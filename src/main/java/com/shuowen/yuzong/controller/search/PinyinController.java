package com.shuowen.yuzong.controller.search;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinTable;
import com.shuowen.yuzong.service.impl.KeyValueService;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.readJson;

@RestController
@RequestMapping ("/api/pinyin/")
public class PinyinController
{
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
            String text = KeyValueService.get("pinyin-style-display-text:" + d.toString());
            var style = d.createStyle(styleParam);

            return APIResponse.success(RichTextUtil.format(text, style, d));
        } catch (Exception e)
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

    @GetMapping ("/{dialect}/table")
    public PinyinTable getTable(@PathVariable String dialect)
    {
        Dialect d = Dialect.of(dialect);
        String text = KeyValueService.get("pinyin-table-display-json:" + d.toString());
        List<Pair<Map<String, String>, List<Map<String, String>>>> data =
                readJson(text, new TypeReference<>() {}, new ObjectMapper());
        return new PinyinTable(data);
    }

//    @GetMapping ("{dialect}/get-tone-preview")
//    public PinyinTable getTone(
//            @PathVariable String dialect,
//            @RequestParam String last)
//    {
//        Dialect d = Dialect.of(dialect);
//        return PinyinTable.getTonePreview(d, last);
//    }


//    @GetMapping("{dialect}/get")
//    public
}
