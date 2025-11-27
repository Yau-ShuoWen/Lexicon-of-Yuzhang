package com.shuowen.yuzong.controller.edit;

import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.controller.APIResponse;
import com.shuowen.yuzong.dao.domain.Character.HanziEdit;
import com.shuowen.yuzong.dao.dto.Character.HanziOutline;
import com.shuowen.yuzong.dao.mapper.Character.MdrCharMapper;
import com.shuowen.yuzong.dao.mapper.Character.NamCharMapper;
import com.shuowen.yuzong.dao.model.Character.CharMdr;
import com.shuowen.yuzong.service.impl.Character.NamHanziServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.shuowen.yuzong.dao.domain.Character.MdrTool.settle;

@RestController
@RequestMapping ("/api/edit/nam")
public class EditNamController
{
    @Autowired
    NamHanziServiceImpl s;

    @Autowired
    MdrCharMapper mdr;

    @Autowired
    NamCharMapper nam;

    @GetMapping ("/byhanzi")
    public List<HanziOutline> filter(@RequestParam String hanzi)
    {
        return s.filter(hanzi);
    }

    @GetMapping ("/byid")
    public HanziEdit hanzifind(@RequestParam Integer id)
    {
        return s.getHanziById(id);
    }

    @PostMapping (value = "/edit")
    public APIResponse<Void> edit(@RequestBody HanziEdit he)
    {
        System.out.println(he);
        try
        {
            s.editHanzi(he);
            return APIResponse.success();
        } catch (Exception e)
        {
            return APIResponse.failure(e.getMessage());
        }
    }

    @GetMapping ("/get-hanzi")
    public List<CharMdr> getHanzi(@RequestParam String hanzi, @RequestParam String hantz)
    {
        return settle(mdr.getInfo(hanzi, hantz));
    }

    @GetMapping ("/get-nearby")
    public Pair<Integer,Integer> getNearBy(@RequestParam Integer id)
    {
        return Pair.of(nam.findPreviousItem(id),nam.findNextItem(id));
    }
}