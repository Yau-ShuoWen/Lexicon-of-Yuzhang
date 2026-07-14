package com.shuowen.yuzong.ysw.data.domain;

import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import com.shuowen.yuzong.util.text.UString;
import com.shuowen.yuzong.Tool.dataStructure.option.Language;
import com.shuowen.yuzong.util.text.ScTcText;
import com.shuowen.yuzong.ysw.data.model.CipherEntity;
import lombok.Data;

import java.util.List;

@Data
public class Cipher
{
    final UString title;
    final UString pronun;
    final List<UString> tag;
    final UString note;
    final Integer id;

    private Cipher(CipherEntity dto, Language l)
    {
        title = ScTcText.get(dto.getMain(), l);
        pronun = ScTcText.get(dto.getPronun(), l);
        tag = ListTool.mapping(
                dto.getTag().split(" "),
                i -> ScTcText.get(i, l)
        );
        note = ScTcText.get(dto.getNote(), l);
        id = dto.getId();
        System.out.println();
    }

    public static Cipher of(CipherEntity dto, Language l)
    {
        return new Cipher(dto, l);
    }

    public static List<Cipher> listOf(List<CipherEntity> dto, Language l)
    {
        return ListTool.mapping(dto, i -> of(i, l));
    }
}
