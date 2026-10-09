package com.shuowen.yuzong.dict.hanzi;

import com.shuowen.yuzong.dict.data.dto.SearchResult;
import com.shuowen.yuzong.dict.hanzi.domain.HanziCreate;
import com.shuowen.yuzong.dict.hanzi.domain.HanziUpdate;
import com.shuowen.yuzong.dict.hanzi.domain.HanziPinyinSuggestion;
import com.shuowen.yuzong.dict.hanzi.model.MdrChar;
import com.shuowen.yuzong.dict.hanzi.service.HanziService;
import com.shuowen.yuzong.dict.hanzi.service.PronunService;
import com.shuowen.yuzong.util.core.Dialect;
import com.shuowen.yuzong.util.err.InvalidPinyinException;
import com.shuowen.yuzong.util.core.Language;
import com.shuowen.yuzong.util.obfuscate.ObfInt;
import com.shuowen.yuzong.util.tuple.APIResponse;
import com.shuowen.yuzong.util.tuple.Maybe;
import com.shuowen.yuzong.util.tuple.MaybeAPIResponse;
import com.shuowen.yuzong.util.tuple.NearbyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/edit/hanzi")
public class EditHanziController
{
    @Autowired
    HanziService hz;

    @Autowired
    PronunService p;

    /**
     * 在编辑之前筛选内容
     */
    @GetMapping ("/filter/{d}")
    public APIResponse<List<SearchResult>> filter(@PathVariable Dialect d, @RequestParam String hanzi)
    {
        return APIResponse.success(hz.getHanziFilterInfo(hanzi, d));
    }


    /**
     * 获得精确的某一个字的信息
     */
    @GetMapping ("/get-info/{d}")
    public APIResponse<HanziUpdate> hanzifind(@PathVariable Dialect d, @RequestParam ObfInt id
    )
    {
        try
        {
            return MaybeAPIResponse.of(
                    Maybe.uncertain(hz.getHanziById(id.decode(), d)),
                    "没有找到汉字"
            );
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }


    /**
     * 提交编辑
     */
    @PostMapping ("/submit/{d}")
    public APIResponse<Void> edit(@PathVariable Dialect d, @RequestBody HanziUpdate he)
    {
        try
        {
            hz.editHanzi(he, d);
            return APIResponse.success();
        } catch (Exception e)
        {
            if (e instanceof InvalidPinyinException)
            {

            }
            else e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }


    /**
     * 获得普通话汉字信息
     */
    @GetMapping ("/get-mandarin/{d}")
    public APIResponse<List<MdrChar>> getMandarin(@PathVariable Dialect d, @RequestParam String sc, @RequestParam String tc)
    {
        return APIResponse.success(p.getHanziMenu(sc, tc, d));
    }


    /**
     * 获得上一号和下一号的号码给跳转
     */
    @GetMapping ("/get-nearby/{d}")
    public APIResponse<NearbyResponse<ObfInt>> getNearBy(@PathVariable Dialect d, @RequestParam ObfInt id)
    {
        try
        {
            return APIResponse.success(NearbyResponse.from(hz.getNearBy(id.decode(), d)));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("/get-word-suggestions/{d}")
    public APIResponse<List<HanziPinyinSuggestion>> getWordSuggestions(
            @PathVariable Dialect d, @RequestParam ObfInt id, @RequestParam Language language)
    {
        try
        {
            return APIResponse.success(hz.getWordPinyinSuggestions(id.decode(), language, d));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("/create/{d}")
    public APIResponse<Void> createHanzi(@PathVariable Dialect d, @RequestBody HanziCreate he
    )
    {
        try
        {
            hz.createHanzi(he, d);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("/get-character/{d}")
    public APIResponse<List<String>> getCharacter(@RequestParam String pinyin, @PathVariable Dialect d)
    {
        return APIResponse.success(hz.getHanziMenu(pinyin, d));
    }
}
