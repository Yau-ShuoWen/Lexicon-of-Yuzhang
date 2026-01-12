package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Character.HanziUpdate;
import com.shuowen.yuzong.data.domain.Character.HanziCreate;
import com.shuowen.yuzong.data.dto.SearchResult;
import com.shuowen.yuzong.data.mapper.Character.PronunMapper;
import com.shuowen.yuzong.data.model.Character.MdrChar;
import com.shuowen.yuzong.service.impl.Character.HanziService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.shuowen.yuzong.data.domain.Character.MdrTool.initWithPinyin;

@RestController
@RequestMapping ("/api/edit/")
public class EditHanziController
{
    @Autowired
    HanziService s;

    @Autowired
    PronunMapper pronun;

    /**
     * 在编辑之前筛选内容
     */
    @GetMapping ("{dialect}/by-hanzi")
    public List<SearchResult> filter(
            @PathVariable String dialect,
            @RequestParam String hanzi
    )
    {
        return s.getHanziFilterInfo(hanzi, Dialect.of(dialect));
    }


    /**
     * 获得精确的某一个字的信息
     */
    @GetMapping ("{dialect}/hanzi/by-id")
    public APIResponse<Maybe<HanziUpdate>> hanzifind(
            @PathVariable String dialect,
            @RequestParam String id
    )
    {
        try
        {
            return APIResponse.success(Maybe.uncertain(
                    s.getHanziById(id, Dialect.of(dialect))
            ));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }


    /**
     * 提交编辑
     */
    @PostMapping ("{dialect}/edit")
    public APIResponse<Void> edit(
            @PathVariable String dialect,
            @RequestBody HanziUpdate he
    )
    {
        try
        {
            s.editHanzi(he, Dialect.of(dialect));
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
    @GetMapping ("{dialect}/get-hanzi")
    public List<MdrChar> getHanzi(
            @PathVariable String dialect,
            @RequestParam String sc,
            @RequestParam String tc
    )
    {
        return initWithPinyin(pronun.getInfoByScTc(sc, tc, Dialect.of(dialect).toString()));
    }


    /**
     * 获得上一号和下一号的号码给跳转
     */
    @GetMapping ("{dialect}/get-nearby")
    public APIResponse<Pair<Maybe<String>, Maybe<String>>>
    getNearBy(@PathVariable String dialect, @RequestParam String id)
    {
        try
        {
            return APIResponse.success(s.getNearBy(id, Dialect.of(dialect)));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }
//
//    @PostMapping ("{dialect}/quick-initialize")
//    public APIResponse<Void> quickInitialize(
//            @PathVariable String dialect,
//            @RequestParam HanziCreate he
//    )
//    {
//        try
//        {
//            s.initHanzi(he, Dialect.of(dialect));
//            return APIResponse.success();
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//            return APIResponse.failure(e.getMessage());
//        }
//    }
}