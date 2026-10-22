package com.shuowen.yuzong.dict.pinyin.controller;

import com.shuowen.yuzong.dict.pinyin.data.PinyinConfig;
import com.shuowen.yuzong.dict.pinyin.data.PinyinDetail;
import com.shuowen.yuzong.dict.pinyin.data.PinyinTable;
import com.shuowen.yuzong.dict.pinyin.PinyinDocumentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.text.TextPinyinIPA;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.MaybeAPIResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/pinyin")
public class PinyinController
{
    private final PinyinDocumentService documents;

    public PinyinController(PinyinDocumentService documents)
    {
        this.documents = documents;
    }

    @GetMapping ("/document/{d}")
    public APIResponse<JsonNode> getDocument(@PathVariable Dialect d)
    {
        return APIResponse.success(documents.get(d));
    }
    @GetMapping ("/preview/{d}/{l}")
    public APIResponse<UString> preview(@PathVariable Dialect d, @PathVariable Language l, String pinyin)
    {
        String py = String.format("[%s]", pinyin);
        String ans = TextPinyinIPA.format(py, new PinyinConfig(l, d), false,
                Maybe.nothing(), false);
        return APIResponse.success(UString.of(ans));
    }

    @GetMapping ("/table/{d}/{l}")
    public APIResponse<PinyinTable> getTable(@PathVariable Dialect d, @PathVariable Language l)
    {
        return APIResponse.success(new PinyinTable(d,l));
    }

    @GetMapping ("/pinyin-detail/{d}/{l}")
    public APIResponse<PinyinDetail> getPinyinDetail(
            @PathVariable Dialect d, @PathVariable Language l,
            @RequestParam String key)
    {
        return MaybeAPIResponse.of(PinyinDetail.of(key, d, l), "没有找到拼音详情");
    }
}
