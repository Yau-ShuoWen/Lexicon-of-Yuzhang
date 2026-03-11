package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.ObfInt;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Character.HanziUpdate;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.model.Character.MdrChar;
import com.shuowen.yuzong.service.impl.Character.HanziService;
import com.shuowen.yuzong.service.impl.Character.PronunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping ("/api/edit/")
public class EditHanziController
{
    @Autowired
    HanziService hz;

    @Autowired
    PronunService p;

    /**
     * 在编辑之前筛选内容
     */
    @GetMapping ("{d}/by-hanzi")
    public List<SearchResult> filter(
            @PathVariable Dialect d,
            @RequestParam String hanzi
    )
    {
        return hz.getHanziFilterInfo(hanzi, d);
    }


    /**
     * 获得精确的某一个字的信息
     */
    @GetMapping ("{d}/hanzi/by-id")
    public APIResponse<Maybe<HanziUpdate>> hanzifind(
            @PathVariable Dialect d,
            @RequestParam ObfInt id
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
    @PostMapping ("{d}/edit")
    public APIResponse<Void> edit(
            @PathVariable Dialect d,
            @RequestBody HanziUpdate he
    )
    {
        try
        {
            hz.editHanzi(he, d);
            return APIResponse.success();
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }


    /**
     * 获得普通话汉字信息
     */
    @GetMapping ("{d}/get-hanzi")
    public List<MdrChar> getHanzi(
            @PathVariable Dialect d,
            @RequestParam String sc,
            @RequestParam String tc
    )
    {
        return p.getHanziMenu(sc, tc, d);
    }


    /**
     * 获得上一号和下一号的号码给跳转
     */
    @GetMapping ("{d}/get-nearby")
    public APIResponse<Twin<Maybe<ObfInt>>>
    getNearBy(@PathVariable Dialect d, @RequestParam ObfInt id)
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
//
//    @PostMapping ("{d}/quick-initialize")
//    public APIResponse<Void> quickInitialize(
//            @PathVariable Dialect d,
//            @RequestParam HanziCreate he
//    )
//    {
//        try
//        {
//            hz.initHanzi(he, d);
//            return APIResponse.success();
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//            return APIResponse.failure(e.getMessage());
//        }
//    }
}