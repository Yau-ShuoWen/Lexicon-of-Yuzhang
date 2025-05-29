package com.shuowen.yuzong.service.impl;

import com.shuowen.yuzong.Linguistics.Format.NamStyle;
import com.shuowen.yuzong.Linguistics.Scheme.NamPinyin;
import com.shuowen.yuzong.dao.mapper.PinyinIPA.NamIPAMapper;
import com.shuowen.yuzong.dao.model.PinyinIPA.NamIPA;
import com.shuowen.yuzong.service.Interface.NamPinyinService;
import org.checkerframework.checker.units.qual.N;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NamPYServiceImpl implements NamPinyinService
{
    @Autowired
    private NamIPAMapper namPY;


    /**
     * 根据拼音找号码
     */
    public String getCode(NamPinyin py)
    {
        return namPY.findByPinyin(py.getPinyin()).getCode();
    }


    /**
     * 比如新加了拼音，就调用一遍，把code是空的全部更新
     */
    public void updateCode()
    {

    }

    /**
     * 如果换了新的编码方式，把code全部更新一遍
     */
    public void reconstructionCode()
    {

    }

    /**
     * 使用ipa资源动态生成ipa
     * @param code 传入数据
     *
     * 把他拆分成声母韵母，
     *
     * @exception
     * */
    public NamIPA constructCode(String code)
    {
        String s1 = code.substring(0, 2) + "~~~";
        String s2 = "~~" + code.substring(2, 5);
        NamIPA a = namPY.consultByCode(s1);
        NamIPA b = namPY.consultByCode(s2);

        try
        {
            NamIPA c = new NamIPA();
            c.setCode(a.getCode().substring(0, 2) + b.getCode().substring(2, 5));
            c.setChiDial(a.getChiDial() + b.getChiDial());
            c.setGanSum(a.getGanSum() + b.getGanSum());
            c.setStandard(a.getStandard() + b.getStandard());
            c.setNcRecord(a.getNcRecord() + b.getNcRecord());
            c.setNcPhon(a.getNcPhon() + b.getNcPhon());
            c.setNcDict(a.getNcDict() + b.getNcDict());
            c.setNcStudy(a.getNcStudy() + b.getNcStudy());
            return c;
        }
        catch (Exception e)
        {
            System.out.println("Exception:"+a);
        }
       return new NamIPA();
    }

    public String getNamPreview(NamStyle style)
    {
        System.out.println(style);

        String s = "枫桥夜泊\n" + "月落乌啼霜满天，" + "江枫渔火对愁眠。\n" + "姑苏城外寒山寺，" + "夜半钟声到客船。\n" +
                NamPinyin.parseAndReplace("[fung1][qieu2][ia5][bok6]", style) + "\n" +
                NamPinyin.parseAndReplace("[nvot6][lok6][u1][ti2][song1][man3][tien1]", style) + "\n" +
                NamPinyin.parseAndReplace("[gong1][fung1][v4][fo3][dui4][ceu2][mien4]", style) + "\n" +
                NamPinyin.parseAndReplace("[gu1][su1][ceen2][uai5][hon2][san1][sii5]", style) + "\n" +
                NamPinyin.parseAndReplace("[ia5][pon5][zung1][seen1][tau5][kak6][con2]", style);


        s = s.replace("//  //", "  ");
        System.out.println(s);

        return s;
    }

    public NamIPA getAllByPinyin(String s)
    {
        return namPY.findByPinyin(s);
    }

    /**
     * 获得列表，会根据code排列
     */
    public List<NamIPA> getAll()
    {
        return namPY.findAll();
    }

    public String getIPA(NamPinyin pinyin, String dict)
    {
        if (pinyin.isInvalid()) return null;
        NamIPA data = getAllByPinyin(pinyin.getPinyin());
        dict = dict.toLowerCase();

        return switch (dict)
        {
            case "ncdict" -> data.getNcDict();
            case "gansum" -> data.getGanSum();
            case "chidial" -> data.getChiDial();
            case "ncrecord" -> data.getNcRecord();
            case "ncstudy" -> data.getNcStudy();
            case "ncphon" -> data.getNcPhon();
            default -> data.getStandard();
        };
    }
}
