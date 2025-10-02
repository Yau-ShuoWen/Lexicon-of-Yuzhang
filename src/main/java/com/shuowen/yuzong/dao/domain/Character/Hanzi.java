package com.shuowen.yuzong.dao.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.Tool.format.Json;
import com.shuowen.yuzong.dao.model.Character.CharEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data
public abstract class Hanzi<T extends UniPinyin,P extends PinyinStyle>
{
    protected Integer id;
    protected String hanzi;
    protected String hantz;
    protected String stdPy;
    protected Integer special;

    protected List<String> similar;
    protected Map<String, Map<String, String>> mulPy;
    protected Map<String, List<String>> pyExplain;
    protected Map<String, String> ipaExp;
    protected Map<String, List<String>> mean;
    protected Map<String, List<String>> note;
    protected Map<String, List<String>> refer;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public Hanzi(CharEntity ch, P style)
    {
        id = ch.getId();
        hanzi = ch.getHanzi();
        hantz = ch.getHantz();
        stdPy = ch.getStdPy();
        special = ch.getSpecial();

        ObjectMapper om = new ObjectMapper();
        similar = Json.safeRead(ch.getSimilar(), new TypeReference<>() {}, om);
        mulPy = Json.safeRead(ch.getMulPy(), new TypeReference<>() {}, om);
        pyExplain = Json.safeRead(ch.getPyExplain(), new TypeReference<>() {}, om);
        ipaExp = Json.safeRead(ch.getIpaExp(), new TypeReference<>() {}, om);
        mean = Json.safeRead(ch.getMean(), new TypeReference<>() {}, om);
        note = Json.safeRead(ch.getNote(), new TypeReference<>() {}, om);
        refer = Json.safeRead(ch.getRefer(), new TypeReference<>() {}, om);

        createdAt = ch.getCreatedAt();
        updatedAt = ch.getUpdatedAt();

        scan(style);
    }

    abstract protected void scan(P style);

}
