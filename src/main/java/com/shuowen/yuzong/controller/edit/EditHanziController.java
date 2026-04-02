package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.error.InvalidPinyinException;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.ObfInt;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Character.HanziCreate;
import com.shuowen.yuzong.data.domain.Character.HanziUpdate;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.model.Character.MdrChar;
import com.shuowen.yuzong.service.impl.Character.HanziService;
import com.shuowen.yuzong.service.impl.Character.PronunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    public List<SearchResult> filter(@PathVariable Dialect d, @RequestParam String hanzi)
    {
        return hz.getHanziFilterInfo(hanzi, d);
    }


    /**
     * 获得精确的某一个字的信息
     */
    @GetMapping ("/get-info/{d}")
    public APIResponse<Maybe<HanziUpdate>> hanzifind(@PathVariable Dialect d, @RequestParam ObfInt id
    )
    {
        try
        {
            return APIResponse.success(Maybe.uncertain(hz.getHanziById(id.decode(), d)));
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
    public List<MdrChar> getMandarin(@PathVariable Dialect d, @RequestParam String sc, @RequestParam String tc)
    {
        return p.getHanziMenu(sc, tc, d);
    }


    /**
     * 获得上一号和下一号的号码给跳转
     */
    @GetMapping ("/get-nearby/{d}")
    public APIResponse<Twin<Maybe<ObfInt>>> getNearBy(@PathVariable Dialect d, @RequestParam ObfInt id)
    {
        try
        {
            return APIResponse.success(hz.getNearBy(id.decode(), d));
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
    public List<String> getCharacter(@RequestParam String pinyin, @PathVariable Dialect d)
    {
        return hz.getHanziMenu(pinyin, d);
    }
}