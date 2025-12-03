package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.data.domain.Character.HanziEdit;
import com.shuowen.yuzong.data.dto.Character.HanziOutline;
import com.shuowen.yuzong.data.mapper.Character.CharMapper;
import com.shuowen.yuzong.data.mapper.Character.MdrCharMapper;
import com.shuowen.yuzong.data.model.Character.CharMdr;
import com.shuowen.yuzong.service.impl.Character.HanziService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.shuowen.yuzong.data.domain.Character.MdrTool.settle;

@RestController
@RequestMapping ("/api/edit/")
public class EditController
{
    @Autowired
    HanziService s;

    @Autowired
    MdrCharMapper mdr;

    @Autowired
    CharMapper m;

    @GetMapping ("{dialect}/byhanzi")
    public List<HanziOutline> filter(
            @PathVariable String dialect,
            @RequestParam String hanzi
    )
    {
        return s.getHanziFilterInfo(hanzi, Dialect.of(dialect));
    }

    @GetMapping ("{dialect}/byid")
    public HanziEdit hanzifind(
            @PathVariable String dialect,
            @RequestParam Integer id
    )
    {
        return s.getHanziById(id, Dialect.of(dialect));
    }

    @PostMapping ("{dialect}/edit")
    public APIResponse<Void> edit(
            @PathVariable String dialect,
            @RequestBody HanziEdit he
    )
    {
        System.out.println(he);
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

    @GetMapping ("/get-hanzi")
    public List<CharMdr> getHanzi(
            @RequestParam String hanzi,
            @RequestParam String hantz)
    {
        return settle(mdr.getInfo(hanzi, hantz));
    }

    @GetMapping ("{dialect}/get-nearby")
    public Pair<Integer, Integer> getNearBy(
            @PathVariable String dialect,
            @RequestParam Integer id)
    {
        return Pair.of(
                m.findPreviousItem(id,Dialect.of(dialect).toString()),
                m.findNextItem(id,Dialect.of(dialect).toString())
        );
    }
}