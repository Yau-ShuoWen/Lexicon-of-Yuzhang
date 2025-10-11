package com.shuowen.yuzong.controller;

import com.shuowen.yuzong.Linguistics.Mandarin.TcSc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 使用hanlp直接繁体转简体
 *
 * @apiNote 没有「简转繁」是因为简转繁不精确，编辑者必须把关繁体版本，才能放心交给程序简化
 */

@RestController
@RequestMapping ("/api/transfer")
public class ProofreadController
{
    @RequestMapping ("/tc")
    public Map<String, String> t2s(@RequestParam String tc)
    {
        return Map.of("sc", TcSc.t2s(tc));
    }

    @RequestMapping ("/tcsc")
    public Map<String, String> t2ts(@RequestParam String tc)
    {
        return Map.of("sc", TcSc.t2s(tc), "tc", tc);
    }
}
