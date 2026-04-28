package com.shuowen.yuzong.controller.info;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Twin;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.data.mapper.Character.HanziMapper;
import com.shuowen.yuzong.data.mapper.Reference.RefMapper;
import com.shuowen.yuzong.data.mapper.Word.CiyuMapper;
import com.shuowen.yuzong.service.impl.KeyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 获得字典信息<br>
 * 不用{@code APIResponse}的原因是，接收一个常量都失败的话，前端有没有那个报错信息都无所谓了
 */
@RestController
@RequestMapping ("/api/info")
public class GetInfoController
{
    @Autowired
    private HanziMapper hz;

    @Autowired
    private CiyuMapper cy;

    @Autowired
    private RefMapper ck;

    @GetMapping ("/get-num/{d}")
    public Map<String, Integer> getInfoNumber(@PathVariable Dialect d)
    {
        var dialect = d.toString();
        return Map.of(
                "hanzi_num", hz.findRowCountInHanziTable(dialect),
                "pinyin_num", hz.findRowCountInPinyinTable(dialect),
                "ciyu_num", cy.findRowCountInCiyuTable(dialect),
                "ref_num", ck.findRowCountInReferTable(dialect)
        );
    }

    @GetMapping ("/get-text/{d}/{l}/{code}")
    public UString welcome(@PathVariable Dialect d, @PathVariable Language l,
                           @PathVariable String code)
    {

        if (code.equals("welcome")) return ScTcText.get(KeyValueService.get("website-greeting:" + d), d, l);

        if (code.contains("explain")) return ScTcText.get(KeyValueService.get(code), l);

        return UString.of("-");
    }

    @GetMapping ("/about-page-text/{d}")
    public Twin<ScTcText> about(@PathVariable Dialect d)
    {
        return Twin.of(
                new ScTcText(KeyValueService.get("website-about:" + d)),
                new ScTcText(KeyValueService.get("website-acknowledgement:" + d))
        );
    }

    @GetMapping ("/get-menu/{code}")
    public List<ScTcText> pronunTagList(@PathVariable String code)
    {
        return JsonTool.readJson(KeyValueService.get("types-of-pronunciation"),
                new TypeReference<>() {}, new ObjectMapper());
    }
}
