package com.shuowen.yuzong.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.dao.mapper.Character.NamCharMapper;
import com.shuowen.yuzong.dao.model.Character.NamChar;
import com.shuowen.yuzong.dto.NamCharDetial;
import com.shuowen.yuzong.dto.NamCharPreview;
import com.shuowen.yuzong.service.Interface.NamHZService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.shuowen.yuzong.Tool.Json.safeRead;

@Service
public class NamHZServiceImpl implements NamHZService
{
    /**
     * 查询汉字
     */
    @Autowired
    private NamCharMapper namHZMapper;

    /**
     * 查询音标
     */
    @Autowired
    private NamPYServiceImpl ipa;

    ObjectMapper om = new ObjectMapper();

    /**
     * 模糊识别汉字，返回基础汉字表格
     */
    public List<NamChar> getNamHZVague(String hanzi)
    {
        return namHZMapper.findByHanziVague(hanzi);
    }

    /**
     * 清晰识别汉字汉字，返回基础汉字表格
     */
    public List<NamChar> getNamHZExactly(String hanzi)
    {
        return namHZMapper.findByHanziExactly(hanzi);
    }

    /**
     * 通过主键找到唯一的汉字
     */
    public NamChar getNamHZById(int id)
    {
        return namHZMapper.selectByPrimaryKey(id);
    }

    public List<NamCharPreview> getMenu(String hanzi)
    {
        return getMenu(hanzi, null);
    }

    public List<NamCharPreview> getMenu(String searchString, NamStyle par)
    {
        List<NamCharPreview> ans = new ArrayList<>();

        List<String> searchChar = new ArrayList<>();

        //把查询内容拆分成各个汉字
        for (int i = 0; i < searchString.length(); )
        {
            int codePoint = searchString.codePointAt(i); // 获取当前的 Unicode code point
            String character = new String(Character.toChars(searchString.codePointAt(i))); // 转为单个字符的 String
            searchChar.add(character);
            i += Character.charCount(codePoint); // 跳过 1 或 2 个 char
        }

        for (String i : searchChar)//遍历每一个汉字
        {
            List<NamChar> AnswerCharList = getNamHZVague(i);//得到模糊查询结果
            if (AnswerCharList == null) continue;

            for (NamChar h : AnswerCharList)
            {
                NamCharPreview dto = new NamCharPreview();

                dto.setHanzi(h.getHanzi());

                NamPinyin stdPy = new NamPinyin(h.getStd_Py(),true);
                dto.setStdPy(par != null ? stdPy.toString(par) : stdPy.toString());

                // 模糊识别匹配的汉字
                List<String> fittingChar = safeRead(h.getFitting_Hanzi(), List.class, om);
                String explain = switch (h.getSpecial())
                {
                    case -1 -> "这个字在本方言无用法  ";
                    case 0 -> "标准用法  ";
                    case 1 -> "特殊用法  ";
                    default -> "";
                };
                if (fittingChar != null && fittingChar.contains(i))
                {
                    explain += "日常可能写「" + i + "」代替 ";
                }

                dto.setExplain(explain);
                ans.add(dto);
            }
        }
        return ans;
    }

    /**
     * 通过汉字查询获得查询返回值数组
     */
    public List<NamCharDetial> getNamHZRequest(String hanzi, NamStyle par)
    {
        List<NamCharDetial> ans = new ArrayList<>();
        List<NamChar> searchAnswer = getNamHZExactly(hanzi);// 这里是精确匹配，因为是结果

        if (searchAnswer == null) return new ArrayList<>();

        for (NamChar i : searchAnswer)
        {
            NamCharDetial dto = new NamCharDetial();

            dto.setHanzi(i.getHanzi());

            NamPinyin stdPy = new NamPinyin(i.getStd_Py(),true);
            dto.setStdPy(par != null ? stdPy.toString(par) : stdPy.toString());

            dto.setFittingHanzi(safeRead(i.getFitting_Hanzi(), List.class, om));

            dto.setMulPy(safeRead(i.getMul_Py(), Map.class, om));
            if (dto.getMulPy() != null)
            {
                String[] tag = {"w", "b", "l", "x", "p"};
                for (String s : tag)
                {
                    NamPinyin py = new NamPinyin(dto.getMulPy().get(s),true);
                    dto.getMulPy().remove(s);
                    if (py.isInvalid()) continue;
                    dto.getMulPy().put(s, par != null ? py.toString(par) : py.toString());
                }
            }

            // 回到NCPY（现在叫PY_Nam）这个表查询
            dto.setIpaExp(safeRead(i.getIpa_exp(), Map.class, om));
            if (dto.getIpaExp() != null)
            {
                String[] dict = {"ncDict", "GanSum", "ChiDial", "ncRecord", "ncStudy", "ncPhon"};
                for (String d : dict)
                {
                    NamPinyin py = new NamPinyin(dto.getIpaExp().get(d),true);//取出来打包成拼音
                    dto.getIpaExp().remove(dto.getIpaExp().get(d));//删除
                    if (py.isInvalid()) continue;
                    dto.getIpaExp().put(d, " //" + ipa.getIPA(py, d) + "// ");
                }
            }

            dto.setMean(safeRead(i.getMean(), List.class, om));
            dto.setNote(safeRead(i.getNote(), List.class, om));
            dto.setRefer(safeRead(i.getRefer(), Map.class, om));

            ans.add(dto);
        }
        return ans;
    }

    public List<NamCharDetial> getNamHZRequest(String hanzi)
    {
        return getNamHZRequest(hanzi, null);
    }
}
