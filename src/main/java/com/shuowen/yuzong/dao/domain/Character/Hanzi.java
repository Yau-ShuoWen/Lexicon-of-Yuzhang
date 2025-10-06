package com.shuowen.yuzong.dao.domain.Character;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Format.PinyinStyle;
import com.shuowen.yuzong.Linguistics.Scheme.UniPinyin;
import com.shuowen.yuzong.dao.model.Character.CharEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

import static com.shuowen.yuzong.Tool.format.JsonTool.*;

@Data
public abstract class Hanzi<T extends UniPinyin,P extends PinyinStyle>
{
    protected Integer id;
    protected String hanzi;
    protected String hantz;
    protected String stdPy;
    protected Integer special;

    protected Map<String,List<String>> similar;
    protected Map<String, Map<String, String>> mulPy;
    protected Map<String, List<String>> pyExplain;
    protected List<Map<String, String>> ipaExp;
    protected Map<String, List<String>> mean;
    protected Map<String, List<String>> note;
    protected Map<String,List<Map<String,String>>> refer;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public static String emptyScTc="{\"sc\": [], \"tc\": []}";

    public Hanzi(CharEntity ch)
    {
        id = ch.getId();
        hanzi = ch.getHanzi();
        hantz = ch.getHantz();
        stdPy = ch.getStdPy();
        special = ch.getSpecial();

        ObjectMapper om = new ObjectMapper();
        similar = readJson(ch.getSimilar(), new TypeReference<>() {}, om);
        mulPy = readJson(ch.getMulPy(), new TypeReference<>() {}, om);
        pyExplain = readJson(ch.getPyExplain(), new TypeReference<>() {}, om);
        ipaExp = readJson(ch.getIpaExp(), new TypeReference<>() {}, om);
        mean = readJson(ch.getMean(), new TypeReference<>() {}, om);
        note = readJson(ch.getNote(), new TypeReference<>() {}, om);
        refer = readJson(ch.getRefer(), new TypeReference<>() {}, om);

        createdAt = ch.getCreatedAt();
        updatedAt = ch.getUpdatedAt();
    }

    public CharEntity transfer()
    {
        CharEntity ans = new CharEntity();
        
        ans.setHanzi(hanzi);
        ans.setHantz(hantz);
        ans.setStdPy(stdPy);
        ans.setSpecial(special);

        ObjectMapper om = new ObjectMapper();
        ans.setSimilar(toJson(similar,om,emptyScTc));
        ans.setMulPy(toJson(mulPy,om,"{}"));
        ans.setPyExplain(toJson(pyExplain,om,emptyScTc));
        ans.setIpaExp(toJson(ipaExp,om));
        ans.setMean(toJson(mean,om,emptyScTc));
        ans.setNote(toJson(note,om,emptyScTc));
        ans.setRefer(toJson(refer,om,emptyScTc));
        
        return ans;
    }

}
