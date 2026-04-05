package com.shuowen.yuzong.Linguistics.Scheme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.shuowen.yuzong.Tool.JavaUtilExtend.ListTool;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode
public class RPinyins
{
    private final List<RPinyin> pinyins;

    private RPinyins(List<RPinyin> pinyins)
    {
        this.pinyins = pinyins;
    }

    @JsonCreator
    public static RPinyins of(List<RPinyin> pinyins)
    {
        return new RPinyins(pinyins);
    }

    @JsonValue
    @Override
    public String toString()
    {
        return String.join("",
                ListTool.mapping(pinyins, RPinyin::toString)
        ).replace("]  [", "] [");
    }
}
