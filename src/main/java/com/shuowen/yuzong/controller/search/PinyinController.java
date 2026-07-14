package com.shuowen.yuzong.controller.search;

import com.shuowen.yuzong.Linguistics.Scheme.SPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.RPinyin;
import com.shuowen.yuzong.Linguistics.Scheme.SPinyins;
import com.shuowen.yuzong.Tool.TextTool.TextPinyinIPA;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinTable;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinDetail;
import com.shuowen.yuzong.data.domain.Pinyin.PinyinChecker;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/pinyin")
public class PinyinController
{
    @GetMapping ("/preview/{d}/{l}")
    public UString preview(@PathVariable Dialect d, @PathVariable Language l, String pinyin)
    {
        return ScTcText.get(TextPinyinIPA.preview(SPinyins.of(pinyin), d, false, false),l);
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
