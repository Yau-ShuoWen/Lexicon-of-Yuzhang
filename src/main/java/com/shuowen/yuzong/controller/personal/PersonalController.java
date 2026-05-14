package com.shuowen.yuzong.controller.personal;

import com.shuowen.yuzong.Tool.dataStructure.option.Alphabet;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.Tool.dataStructure.text.ScTcText;
import com.shuowen.yuzong.data.domain.Personal.AlphabetTable;
import com.shuowen.yuzong.data.domain.Personal.AlphabetTransfer;
import com.shuowen.yuzong.data.domain.Personal.Cipher;
import com.shuowen.yuzong.data.mapper.Personal.PersonalMapper;
import com.shuowen.yuzong.service.impl.KV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping ("/alphabet/table/{d}")
    public AlphabetTable getTable(@PathVariable Alphabet d)
    {
        return new AlphabetTable(d);
    }

    @GetMapping ("/alphabet/list")
    public List<Alphabet> getList()
    {
        return Alphabet.getList();
    }

    @GetMapping ("/alphabet/transfer/{alphabet}")
    public String transfer(@PathVariable Alphabet alphabet, @RequestParam String s)
    {
        return AlphabetTransfer.format(alphabet, s);
    }
}
