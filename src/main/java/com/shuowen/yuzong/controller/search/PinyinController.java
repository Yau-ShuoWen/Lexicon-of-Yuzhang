package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Linguistics.Format.PinyinParam;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Tool.RichTextUtil;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.option.Scheme;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinTable;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinDetail;
import com.shuowen.yuzong.service.impl.KeyValueService;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/pinyin/")
public class PinyinController
{
    @GetMapping ("/style-init/{d}")
    public PinyinStyle pinyin(@PathVariable Dialect d, @RequestParam Scheme SchemeParam)
    {
        return d.createStyle(PinyinParam.of(SchemeParam));
    }

    /**
     * 传入拼音配置，预览效果
     */
    @PostMapping ("/preview/{d}")
    public APIResponse<String> preview(@PathVariable Dialect d,
                                       @RequestBody Map<String, Object> styleParam)
    {
        try
        {
            String text = KeyValueService.get("pinyin-style-display-text:" + d.toString());
            var style = d.createStyle(styleParam);

            return APIResponse.success(RichTextUtil.format(text, style, d));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("/normalize/{d}")
    public Triple<Integer, RPinyin, SPinyin> normalizeCheck(
            @PathVariable Dialect d,
            @RequestParam SPinyin pinyin)
    {
        return PinyinChecker.suggestively(pinyin, d);
    }

    @GetMapping ("/table/{d}")
    public PinyinTable getTable(@PathVariable Dialect d)
    {
        return new PinyinTable(d);
    }

    @GetMapping ("/pinyin-detail/{d}/{l}")
    public Maybe<PinyinDetail> getPinyinDetail(
            @PathVariable Dialect d, @PathVariable Language l,
            @RequestParam String key)
    {
        return PinyinDetail.of(key, d, l);
    }
}
