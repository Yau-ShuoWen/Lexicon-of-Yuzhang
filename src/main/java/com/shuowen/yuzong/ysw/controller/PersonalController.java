package com.shuowen.yuzong.ysw.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.Tool.dataStructure.Maybe;
import com.shuowen.yuzong.Tool.dataStructure.UString;
import com.shuowen.yuzong.ysw.linguistic.Alphabet;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Pair;
import com.shuowen.yuzong.Tool.dataStructure.tuple.Triple;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.ysw.data.domain.AlphabetTable;
import com.shuowen.yuzong.ysw.data.domain.AlphabetTransfer;
import com.shuowen.yuzong.ysw.data.domain.Cipher;
import com.shuowen.yuzong.ysw.data.mapper.PersonalMapper;
import com.shuowen.yuzong.service.impl.KV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/api/personal/")
public class PersonalController
{
    @Autowired
    PersonalMapper m;

    @GetMapping ("/hello")
    public ScTcText greeting()
    {
        return new ScTcText(KV.get("website-greeting:ysw"));
    }

    @GetMapping ("/dict/search/{l}")
    public List<Cipher> query(@PathVariable Language l, @RequestParam String q)
    {
        return Cipher.listOf(m.search(q), l);
    }

    @GetMapping ("/dict/item/{l}/")
    public Cipher item(@PathVariable Language l, @RequestParam Integer id)
    {
        return Cipher.of(m.selectById(id), l);
    }

    @GetMapping ("/alphabet/table/{alphabet}/{l}")
    public Triple<Alphabet.AlphabetT, UString, AlphabetTable> getTable(@PathVariable Alphabet alphabet, @PathVariable Language l)
    {
        return Triple.of(
                alphabet.toTrans(l),
                ScTcText.get(Maybe.uncertain(KV.get("alphabet-introduce-text:" + alphabet)).getValueOrDefault(""), l),
                new AlphabetTable(alphabet, l)
        );
    }

    @GetMapping ("/alphabet/catalog/{l}")
    public List<Pair<String, List<Map<String, String>>>> getCatalog(@PathVariable Language l)
    {
        List<Pair<String, List<Map<String, String>>>> list = JsonTool.readJson(KV.get("alphabet-catalog"), new TypeReference<>() {});

        return ListTool.mapping(list, i ->
        {
            var left = ScTcText.get(i.getLeft(), l);
            var right = ListTool.mapping(i.getRight(), item ->
                    Map.of(
                            "name", ScTcText.get(item.get("name"), l).toString(),
                            "example", ScTcText.get(item.get("example"), l).toString(),
                            "url", item.get("url")
                    )
            );
            return Pair.of(left.toString(), right);
        });
    }

    @GetMapping ("/alphabet/transfer/{alphabet}/{l}")
    public String transfer(@PathVariable Alphabet alphabet, @PathVariable Language l,
                           @RequestParam String funName, @RequestParam String s
    )
    {
        return AlphabetTransfer.format(alphabet, l, funName, s);
    }
}
