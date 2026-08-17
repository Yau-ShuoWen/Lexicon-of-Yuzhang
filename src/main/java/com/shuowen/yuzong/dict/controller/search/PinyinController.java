package com.shuowen.yuzong.dict.controller.search;

import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.dict.data.domain.Pinyin.PinyinConfig;
import com.shuowen.yuzong.dict.data.domain.Pinyin.PinyinDetail;
import com.shuowen.yuzong.dict.data.domain.Pinyin.PinyinTable;
import com.shuowen.yuzong.util.text.TextPinyinIPA;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.util.tuple.Maybe;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/api/pinyin")
public class PinyinController
{
    @GetMapping ("/preview/{d}/{l}")
    public UString preview(@PathVariable Dialect d, @PathVariable Language l, String pinyin)
    {
        String py = String.format("[%s]", pinyin);
        String ans = TextPinyinIPA.format(py, new PinyinConfig(l, d), false,
                Maybe.nothing(), false);
        return UString.of(ans);
    }

    @GetMapping ("/table/{d}/{l}")
    public PinyinTable getTable(@PathVariable Dialect d, @PathVariable Language l)
    {
        return new PinyinTable(d,l);
    }

    @GetMapping ("/pinyin-detail/{d}/{l}")
    public Maybe<PinyinDetail> getPinyinDetail(
            @PathVariable Dialect d, @PathVariable Language l,
            @RequestParam String key)
    {
        return PinyinDetail.of(key, d, l);
    }
}
