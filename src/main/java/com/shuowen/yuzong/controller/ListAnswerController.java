package com.shuowen.yuzong.controller;

import com.shuowen.yuzong.dto.NamCharPreview;
import com.shuowen.yuzong.service.impl.NamHZServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class ListAnswerController
{
    @Autowired
    NamHZServiceImpl service;

    @GetMapping ("/byhanzi")
    public List<NamCharPreview> hhh(@RequestParam String hanzi)
    {
        return service.getMenu(hanzi);
    }
}

