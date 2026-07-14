package com.shuowen.yuzong.service.impl.IPA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shuowen.yuzong.Tool.dataStructure.option.Dialect;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.Tool.format.JsonTool;
import com.shuowen.yuzong.data.mapper.IPA.IPAMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PinyinService
{
    @Autowired
    private IPAMapper m;

    public List<String> getKey(Dialect d)
    {
        return m.getEditKey(d.toString());
    }

    public ScTcText getNote(Dialect d, String key)
    {
        return JsonTool.readJson(m.getNote(d.toString(),key), new TypeReference<>() {});
    }

    public void updateNote(Dialect d, String key, ScTcText note)
    {
        m.updateNote(d.toString(), key, JsonTool.toJson(note));
    }
}
