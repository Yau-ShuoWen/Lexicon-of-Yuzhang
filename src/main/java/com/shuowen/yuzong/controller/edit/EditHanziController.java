package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Character.HanziEdit;
import com.shuowen.yuzong.data.dto.Character.HanziOutline;
import com.shuowen.yuzong.data.mapper.Character.CharMapper;
import com.shuowen.yuzong.data.mapper.Character.PronunMapper;
import com.shuowen.yuzong.data.model.Character.CharMdr;
import com.shuowen.yuzong.service.impl.Character.HanziService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.shuowen.yuzong.data.domain.Character.MdrTool.settle;

@RestController
@RequestMapping ("/api/edit/")
public class EditHanziController
{
    @Autowired
    HanziService s;

    @Autowired
    PronunMapper pronun;

    @Autowired
    CharMapper m;

    @GetMapping ("{dialect}/by-hanzi")
    public List<HanziOutline> filter(
            @PathVariable String dialect,
            @RequestParam String hanzi
    )
    {
        return s.getHanziFilterInfo(hanzi, Dialect.of(dialect));
    }

    @GetMapping ("{dialect}/hanzi/by-id")
    public APIResponse<Result<HanziEdit>> hanzifind(
            @PathVariable String dialect,
            @RequestParam Integer id
    )
    {
        try
        {
            return APIResponse.success(Result.ofNullable(
                    s.getHanziById(id, Dialect.of(dialect))
            ));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }

    @PostMapping ("{dialect}/edit")
    public APIResponse<Void> edit(
            @PathVariable String dialect,
            @RequestBody HanziEdit he
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


    @GetMapping ("{dialect}/get-hanzi")
    public List<CharMdr> getHanzi(
            @PathVariable String dialect,
            @RequestParam String hanzi,
            @RequestParam String hantz,
            @RequestParam (required = false, defaultValue = "0") Integer id
    )
    {
        return settle(pronun.getMandarinInfo(hanzi, hantz, id, Dialect.of(dialect).toString()));
    }


    @GetMapping ("{dialect}/get-nearby")
    public APIResponse<Pair<Result<Integer>, Result<Integer>>>
    getNearBy(@PathVariable String dialect, @RequestParam Integer id)
    {
        try
        {
            return APIResponse.success(Pair.of(
                    Result.ofNullable(m.findPreviousId(id, Dialect.of(dialect).toString())),
                    Result.ofNullable(m.findNextId(id, Dialect.of(dialect).toString()))
            ));
        } catch (Exception e)
        {
            e.printStackTrace();
            return APIResponse.failure(e.getMessage());
        }
    }
}